package cz.cvut.fel.ida.learning.results.metrics.Jesse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Decompiled from Jesse (thanks!), as I really didn't want to generate any files during the run just to calculate AUC (= use you jar).
 * Moreover I was only interested in your AUC, since it is higher than the regular empirical/Wilcoxon one! :) that's kinda weird...
 */
public class Confusions extends ArrayList<ROCpoint> {
    private static final Logger LOG = Logger.getLogger(Confusions.class.getName());

    private double totalPos;
    private double totalNeg;

    public Confusions(double pos, double neg) {
        this.totalPos = pos;
        this.totalNeg = neg;

    }

    public void addPRPoint(double var1, double var3) throws NumberFormatException {
        if (var1 <= 1.0D && var1 >= 0.0D && var3 <= 1.0D && var3 >= 0.0D) {
            double var5 = var1 * this.totalPos;
            double var7 = (var5 - var3 * var5) / var3;
            ROCpoint var9 = new ROCpoint(var5, var7);
            if (!this.contains(var9)) {
                this.add(var9);
            }

        } else {
            throw new NumberFormatException();
        }
    }

    public void addROCPoint(double var1, double var3) throws NumberFormatException {
        if (var1 <= 1.0D && var1 >= 0.0D && var3 <= 1.0D && var3 >= 0.0D) {
            double var5 = var3 * this.totalPos;
            double var7 = var1 * this.totalNeg;
            ROCpoint var9 = new ROCpoint(var5, var7);
            if (!this.contains(var9)) {
                this.add(var9);
            }

        } else {
            throw new NumberFormatException();
        }
    }

    public void addPoint(double var1, double var3) throws NumberFormatException {
        if (var1 >= 0.0D && var1 <= this.totalPos && var3 >= 0.0D && var3 <= this.totalNeg) {
            ROCpoint var5 = new ROCpoint(var1, var3);
            if (!this.contains(var5)) {
                this.add(var5);
            }

        } else {
            throw new NumberFormatException();
        }
    }

    public void sort() {

        if (this.size() == 0) {
            System.err.println("ERROR: No data to sort....");
        } else {
            ROCpoint[] var1 = new ROCpoint[this.size()];
            int var2 = 0;

            while (this.size() > 0) {
                var1[var2++] = (ROCpoint) this.get(0);
                this.remove(0);
            }

            Arrays.sort(var1);

            for (int var3 = 0; var3 < var1.length; ++var3) {
                this.add(var1[var3]);
            }

            ROCpoint var7;
            for (var7 = (ROCpoint) this.get(0); var7.posPosition < 0.001D && var7.posPosition > -0.001D; var7 = (ROCpoint) this.get(0)) {
                this.remove(0);
            }

            double var4 = var7.negPosition / var7.posPosition;
            ROCpoint var6 = new ROCpoint(1.0D, var4);
            if (!this.contains(var6) && var7.posPosition > 1.0D) {
                this.add(0, var6);
            }

            var6 = new ROCpoint(this.totalPos, this.totalNeg);
            if (!this.contains(var6)) {
                this.add(var6);
            }

        }
    }

    public void interpolate() {

        if (this.size() == 0) {
            System.err.println("ERROR: No data to interpolate....");
        } else {
            for (int var1 = 0; var1 < this.size() - 1; ++var1) {
                ROCpoint var2 = (ROCpoint) this.get(var1);
                ROCpoint var3 = (ROCpoint) this.get(var1 + 1);
                double var4 = var3.posPosition - var2.posPosition;
                double var6 = var3.negPosition - var2.negPosition;
                double var8 = var6 / var4;
                double var10 = var2.posPosition;

                ROCpoint var16;
                for (double var12 = var2.negPosition; Math.abs(var2.posPosition - var3.posPosition) > 1.001D; var2 = var16) {
                    double var14 = var12 + (var2.posPosition - var10 + 1.0D) * var8;
                    var16 = new ROCpoint(var2.posPosition + 1.0D, var14);
                    ++var1;
                    this.add(var1, var16);
                }
            }

        }
    }

    public double calculateAUCPR(double minRecall) {

        if (minRecall >= 0.0D && minRecall <= 1.0D) {
            if (this.size() == 0) {
                System.err.println("ERROR: No data to calculate....");
                return 0.0D;
            } else {
                double var3 = minRecall * this.totalPos;
                int var5 = 0;
                ROCpoint var6 = (ROCpoint) this.get(var5);
                ROCpoint var7 = null;

//                try {
                while (var6.posPosition < var3) {
                    var7 = var6;
                    ++var5;
                    var6 = (ROCpoint) this.get(var5);
                }
//                } catch (ArrayIndexOutOfBoundsException var24) {
//                    System.err.println("ERROR: minRecall out of bounds - exiting...");
//                    System.exit(-1);
//                }

                double var8 = (var6.posPosition - var3) / this.totalPos;
                double var10 = var6.posPosition / (var6.posPosition + var6.negPosition);
                double aucPR = var8 * var10;
                double var16;
                double var18;
                double var20;
                double var22;
                if (var7 != null) {
                    double var14 = var6.posPosition / this.totalPos - var7.posPosition / this.totalPos;
                    var16 = var6.posPosition / (var6.posPosition + var6.negPosition) - var7.posPosition / (var7.posPosition + var7.negPosition);
                    var18 = var16 / var14;
                    var20 = var7.posPosition / (var7.posPosition + var7.negPosition) + var18 * (var3 - var7.posPosition) / this.totalPos;
                    var22 = 0.5D * var8 * (var20 - var10);
                    aucPR += var22;
                }

                var8 = var6.posPosition / this.totalPos;

                for (int var25 = var5 + 1; var25 < this.size(); ++var25) {
                    ROCpoint var15 = (ROCpoint) this.get(var25);
                    var16 = var15.posPosition / this.totalPos;
                    var18 = var15.posPosition / (var15.posPosition + var15.negPosition);
                    var20 = (var16 - var8) * var18;
                    var22 = 0.5D * (var16 - var8) * (var10 - var18);
                    aucPR += var20 + var22;
                    var8 = var16;
                    var10 = var18;
                }

//                System.out.println("Area Under the Curve for Precision - Recall is " + aucPR);
                return aucPR;
            }
        } else {
            System.err.println("ERROR: invalid minRecall, must be between 0 and 1 - returning 0");
            return 0.0D;
        }
    }

    public double calculateAUCROC() {

        if (this.size() == 0) {
            System.err.println("ERROR: No data to calculate....");
            return 0.0D;
        } else {
            ROCpoint ROCpoint = (ROCpoint) this.get(0);
            double prevPosRatio = ROCpoint.posPosition / this.totalPos;
            double prevNegRatio = ROCpoint.negPosition / this.totalNeg;
            double auc = 0.5D * prevPosRatio * prevNegRatio;

            for (int i = 1; i < this.size(); ++i) {
                ROCpoint currPNpoint = (ROCpoint) this.get(i);
                double currPosRatio = currPNpoint.posPosition / this.totalPos;
                double currNegRatio = currPNpoint.negPosition / this.totalNeg;
                double top = (currPosRatio - prevPosRatio) * currNegRatio;
                double bottom = 0.5D * (currPosRatio - prevPosRatio) * (currNegRatio - prevNegRatio);
                auc += top - bottom;
                prevPosRatio = currPosRatio;
                prevNegRatio = currNegRatio;
            }

            auc = 1.0D - auc;
//            System.out.println("Area Under the Curve for ROC is " + auc);
            return auc;
        }
    }
}
