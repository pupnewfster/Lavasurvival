package me.eddiep.minecraft.ls.glicko;

import me.eddiep.minecraft.ls.system.PFunction;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class GlickoRank {

    /**
     * The amount of ranked games a player must play before getting ranked
     */
    public static final int PLACEMENT_GAME_COUNT = 15;

    /**
     * The amount of ranked games a player must play before calculating a new rank
     */
    public static final int RANKED_GAME_COUNT = 10;

    //=== RANKING CONSTANTS ===
    public static final double SCALING_FACTOR = 173.7378;

    private double tau;

    //rating
    private double rating;
    //rating deviation
    private double rd;
    //volatillity
    private double vol;

    private long pID;

    private List<Double> workingOutcomes = new ArrayList<>();
    private List<Double> workingAdvRanks = new ArrayList<>();
    private List<Double> workingAdvRds = new ArrayList<>();

    private List<Double> outcomes = new ArrayList<>();
    private List<Double> advRanks = new ArrayList<>();
    private List<Double> advRds = new ArrayList<>();

    private boolean isRanked;

    private long lastUpdate;

    private final Object lock = new Object();

    GlickoRank(int rating, double rd, double vol) {
        setRating(rating);
        setRd(rd);
        setVol(vol);

        this.tau = Glicko2.getInstance().getTau();
        isRanked = false;
    }

    private GlickoRank() { }

    public void save(String baseKey, FileConfiguration configuration) {
        configuration.set(baseKey + ".tau", tau);
        configuration.set(baseKey + ".rating", getRating());
        configuration.set(baseKey + ".rd", rd);
        configuration.set(baseKey + ".vol", vol);
        configuration.set(baseKey + ".outcomes", outcomes);
        configuration.set(baseKey + ".advRanks", advRanks);
        configuration.set(baseKey + ".advRds", advRds);
        configuration.set(baseKey + ".isRanked", isRanked);
    }

    public void load(String baseKey, FileConfiguration configuration) {
        tau = configuration.getDouble(baseKey + ".tau", Glicko2.getInstance().getTau());

        setRating(configuration.getDouble(baseKey + ".rating", Glicko2.getInstance().getDefaultRating()));

        rd = configuration.getDouble(baseKey + ".rd", Glicko2.getInstance().getDefaultRd());
        vol = configuration.getDouble(baseKey + ".vol", Glicko2.getInstance().getDefaultVol());
        outcomes = configuration.getDoubleList(baseKey + ".outcomes");
        advRanks = configuration.getDoubleList(baseKey + ".advRanks");
        advRds = configuration.getDoubleList(baseKey + ".advRds");
        isRanked = configuration.getBoolean(baseKey + ".isRanked", false);
    }

    public double getRawRating() {
        return rating;
    }

    public double getRawRd() {
        return rd;
    }

    public int getRating() {
        return (int) (this.rating * SCALING_FACTOR + Glicko2.getInstance().getDefaultRating());
    }

    public void setRating(double rating) {
        this.rating = ((rating - Glicko2.getInstance().getDefaultRating()) / SCALING_FACTOR);
    }

    public double getRd() {
        return rd * SCALING_FACTOR;
    }

    public void setRd(double rd) {
        this.rd = rd / SCALING_FACTOR;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public void addResult(Rankable opponent, double outcome) {
        synchronized (lock) {
            outcomes.add(outcome);
            advRanks.add(opponent.getRanking().rating);
            advRds.add(opponent.getRanking().rd);
        }
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public boolean shouldUpdate() {
        if (!isRanked) {
            return outcomes.size() > PLACEMENT_GAME_COUNT;
        } else {
            return outcomes.size() > RANKED_GAME_COUNT;
        }
    }

    public boolean hasPlayed() {
        return outcomes.size() > 0;
    }

    public void update() {
        if (!hasPlayed()) {
            rd = Math.sqrt((rd * rd) + (vol * vol));
            lastUpdate = System.currentTimeMillis();
            return;
        }

        if (!shouldUpdate())
            return;

        synchronized (lock) {
            workingOutcomes = new ArrayList<>(outcomes);
            workingAdvRanks = new ArrayList<>(advRanks);
            workingAdvRds = new ArrayList<>(advRds);

            outcomes.clear();
            advRanks.clear();
            advRds.clear();
        }

        double v = variance();

        double delta = delta(v);

        vol = calculateVolatility(v, delta);

        rd = Math.sqrt((rd * rd) + (vol * vol));

        rd = 1.0 / Math.sqrt((1.0 / (rd * rd)) + (1.0 / v));

        double sum = 0.0;
        for (int i = 0; i < workingOutcomes.size(); i++) {
            sum += G(workingAdvRds.get(i)) * (workingOutcomes.get(i) - E(workingAdvRanks.get(i), workingAdvRds.get(i)));
        }

        rating += (rd * rd) * sum;
        lastUpdate = System.currentTimeMillis();

        workingOutcomes.clear();
        workingAdvRds.clear();
        workingAdvRanks.clear();

        isRanked = true;
    }

    public long getOwnerID() {
        return pID;
    }

    private double variance() {
        double sum = 0;
        for (int i = 0; i < workingOutcomes.size(); i++) {
            double temp = E(workingAdvRanks.get(i), workingAdvRds.get(i));
            double temp2 = G(workingAdvRds.get(i));
            temp2 = temp2 * temp2;
            sum += temp2 * temp * (1 - temp);
        }

        return sum;
    }

    private double E(double rating, double rds) {
        return 1.0 / (1.0 + Math.exp(-1 * G(rds) * (this.rating - rating)));
    }

    private double G(double rd) {
        return 1.0 / Math.sqrt(1 + 3 * (rd * rd) / (Math.PI * Math.PI));
    }

    private double delta(double v) {
        double sum = 0;
        for (int i = 0; i < workingOutcomes.size(); i++) {
            sum += G(workingAdvRds.get(i)) * (workingOutcomes.get(i) - E(workingAdvRanks.get(i), workingAdvRds.get(i)));
        }

        return v * sum;
    }

    private PFunction<Double, Double> makef(final double delta, final double v, final double a) {
        return new PFunction<Double, Double>() {
            @Override
            public Double run(Double x) {
                return ( Math.exp(x) * ( (delta * delta) - (rd * rd) - v - Math.exp(x) ) /
                        (2.0 * Math.pow( (rd * rd) + v + Math.exp(x), 2) )) -
                        ( ( x - a ) / (tau * tau) );
            }
        };
    }

    private double calculateVolatility(double v, double delta) {
        double A = Math.log(vol * vol);
        PFunction<Double, Double> f = makef(delta, v, A);
        double epsilon = 0.000001;

        double B, k;
        if ((delta * delta) >  (rd * rd) + v){
            B = Math.log((delta * delta) -  (rd * rd) - v);
        } else {
            k = 1;
            while (f.run(A - (k * tau)) < 0){
                k = k + 1;
            }
            B = A - (k * this.tau);
        }

        double fA = f.run(A);
        double fB = f.run(B);

        double C, fC;
        while (Math.abs(B - A) > epsilon){
            C = A + (( (A-B)*fA ) / (fB - fA));
            //C = (A + ((A - B) * fA)) / (fB - fA);
            fC = f.run(C);
            if (fC * fB < 0){
                A = B;
                fA = fB;
            } else {
                fA = fA / 2.0;
            }
            B = C;
            fB = fC;
        }
        return Math.exp(A/2.0);
    }

    /*private double tau;
    private int ranking;
    private double rd;
    private double vol;
    private List<Integer> workingAdvRanks = new ArrayList<>();
    private List<Double> workingAdvRds = new ArrayList<>();
    private List<Double> workingOutcomes = new ArrayList<>();

     */

    public Rankable toRankable() {
        return new Rankable() {
            @Override
            public GlickoRank getRanking() {
                return GlickoRank.this;
            }
        };
    }

    public int getGameCount() {
        return outcomes.size();
    }

    public boolean isRanked() {
        return isRanked;
    }
}