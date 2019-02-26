/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.utils;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Functions for working with two-dimensional arrays of numbers.
 *
 * Not optimal from the "numerics" point of view.
 *
 * @author admin
 */
public class MatrixUtils {

    /**
     * Checks if all elements of the matrix are 0.
     * @param matrix the matrix
     * @return true if all elements of the matrix are zero, false otherwise
     */
    public static boolean isZero(double[][] matrix){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix.length; j++){
                if (matrix[i][j] != 0){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if all elements of the matrix are 0.
     * @param matrix the matrix
     * @return true if all elements of the matrix are zero, false otherwise
     */
    public static boolean isZero(int[][] matrix){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix.length; j++){
                if (matrix[i][j] != 0){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Creates an n-times-n matrix with 1 on diagonal and zeros elsewhere.
     * @param n dimension of the matrix
     * @return n-times-n matrix with 1 on diagonal and zeros elsewhere
     */
    public static double[][] eye(int n){
        double[][] e = new double[n][n];
        for (int i = 0; i < e.length; i++){
            e[i][i] = 1;
        }
        return e;
    }

    public static double[][] eye(int n, double value){
        double[][] e = new double[n][n];
        for (int i = 0; i < e.length; i++){
            e[i][i] = value;
        }
        return e;
    }

    /**
     * Creates an n-times-n integer matrix with 1 on diagonal and zeros elsewhere.
     * @param n dimension of the matrix
     * @return n-times-n integer matrix with 1 on diagonal and zeros elsewhere
     */
    public static int[][] integerEye(int n){
        int[][] e = new int[n][n];
        for (int i = 0; i < e.length; i++){
            e[i][i] = 1;
        }
        return e;
    }

    /**
     * Creates a random boolean matrix
     * @param rows number of rows
     * @param columns number of columns
     * @return random boolean matrix
     */
    public static boolean[][] randomBooleanMatrix(int rows, int columns){
        boolean[][] matrix = new boolean[rows][];
        for (int i = 0; i < rows; i++){
            matrix[i] = VectorUtils.randomBooleanVector(columns);
        }
        return matrix;
    }

    /**
     * Creates a random integer matrix. The values are from 0 to Integer,MAX_VALUE
     * @param rows number of rows
     * @param columns number of columns
     * @return random integer matrix
     */
    public static int[][] randomintegerMatrix(int rows, int columns){
        int[][] matrix = new int[rows][];
        for (int i = 0; i < rows; i++){
            matrix[i] = VectorUtils.randomVector(columns);
        }
        return matrix;
    }

    /**
     * Creates a random matrix. The values are from 0 to 1
     * @param rows number of rows
     * @param columns number of columns
     * @return random integer matrix
     */
    public static double[][] randomDoubleMatrix(int rows, int columns){
        double[][] matrix = new double[rows][columns];
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
    
    /**
     * Checks if two given matrices are equal.
     * @param a a matrix
     * @param b a matrix
     * @return true if the two matrices are equal, false otherwise
     */
    public static boolean equal(double[][] a, double[][] b){
        if (a.length != b.length){
            return false;
        }
        for (int i = 0; i < a.length; i++){
            if (a[i].length != b[i].length){
                return false;
            }
            for (int j = 0; j < a[i].length; j++){
                if (a[i][j] != b[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if two given matrices are equal.
     * @param a a matrix
     * @param b a matrix
     * @return true if the two matrices are equal, false otherwise
     */
    public static boolean equal(int[][] a, int[][] b){
        if (a.length != b.length){
            return false;
        }
        for (int i = 0; i < a.length; i++){
            if (a[i].length != b[i].length){
                return false;
            }
            for (int j = 0; j < a[i].length; j++){
                if (a[i][j] != b[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if two given matrices are equal.
     * @param a a matrix
     * @param b a matrix
     * @return true if the two matrices are equal, false otherwise
     */
    public static boolean equal(boolean[][] a, boolean[][] b){
        if (a.length != b.length){
            return false;
        }
        for (int i = 0; i < a.length; i++){
            if (a[i].length != b[i].length){
                return false;
            }
            for (int j = 0; j < a[i].length; j++){
                if (a[i][j] != b[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Converts a matrix to string.
     * @param m the matrix
     * @return string representation of the matrix
     */
    public static String doubleMatrixToString(double[][] m){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.length; i++){
            sb.append(VectorUtils.doubleArrayToString(m[i]));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Converts a matrix to string.
     * @param m the matrix
     * @return string representation of the matrix
     */
    public static String booleanMatrixToString(boolean[][] m){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.length; i++){
            sb.append(VectorUtils.booleanArrayToString(m[i]));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Converts a matrix to string which can be read by Matlab software.
     * @param matrix the matrix
     * @return string representation of the matrix
     */
    public static String doubleMatrixToMatlabString(double[][] matrix){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                sb.append(matrix[i][j]);
                if (j < matrix[i].length-1){
                    sb.append(" ");
                }
            }
            if (i < matrix.length-1){
                sb.append("; ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Converts a matrix to string which can be read by Matlab software.
     * @param matrix the matrix
     * @return string representation of the matrix
     */
    public static String intMatrixToMatlabString(int[][] matrix){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                sb.append(matrix[i][j]);
                if (j < matrix[i].length-1){
                    sb.append(" ");
                }
            }
            if (i < matrix.length-1){
                sb.append("; ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Converts a matrix to string.
     * @param m the matrix
     * @return string representation of the matrix
     */
    public static String intMatrixToString(int[][] m){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.length; i++){
            sb.append(VectorUtils.intArrayToString(m[i]));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Computes hashCode of a given matrix
     * @param matrix the matrix
     * @return the computed hash code
     */
    public static int deepHashCode(double[][] matrix){
        int retVal = 0;
        for (int i = 0; i < matrix.length; i++){
            retVal += Arrays.hashCode(matrix[i]);
        }
        return retVal;
    }

    /**
     * Transposes a matrix (i.e. exchanges rows by columns)
     * @param matrix the matrix to be transposed
     * @return the transposed matrix
     */
    public static double[][] transpose(double[][] matrix){
        double[][] retVal = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){
                retVal[j][i] = matrix[i][j];
            }
        }
        return retVal;
    }

    /**
     * Computes sum of two equal-sized matrices.
     * @param a 
     * @param b
     * @return matrix which is sum of <em>a</em> and <em>b</em>
     */
    public static double[][] sum(double[][] a, double[][] b){
        double[][] retVal = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[0].length; j++){
                retVal[i][j] = a[i][j]+b[i][j];
            }
        }
        return retVal;
    }

    /**
     * Adds values of the second matrix to values of the first matrix.
     * @param a the first matrix
     * @param b the second matrix
     */
    public static void add(double[][] a, double[][] b){
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                a[i][j] += b[i][j];
            }
        }
    }

    /**
     * Adds values of the second matrix to values of the first matrix.
     * @param a the first matrix
     * @param b the second matrix
     */
    public static void add(double[][] a, double[] b){
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                a[i][j] += b[j];
            }
        }
    }

    /**
     * Computes product of a matrix and a number
     * @param a the matrix
     * @param b the number
     * @return matrix which is sum of <em>a</em> and <em>b</em>
     */
    public static double[][] product(double[][] a, double b){
        double[][] retVal = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[0].length; j++){
                retVal[i][j] = a[i][j]*b;
            }
        }
        return retVal;
    }

    /**
     * Multiplies values of the matrix by the number <em>b</em>
     * @param a the matrix
     * @param b the number
     */
    public static void multiply(double[][] a, double b){
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[0].length; j++){
                a[i][j] *= b;
            }
        }
    }

    /**
     * Computes trace of the matrix
     * @param matrix
     * @return trace of the matrix
     */
    public static double trace(double[][] matrix){
        double trace = 0;
        for (int i = 0; i < matrix.length; i++){
            trace += matrix[i][i];
        }
        return trace;
    }

    /**
     * Swaps two rows of a matrix.
     * @param matrix the matrix
     * @param i1 index of the first row
     * @param i2 index of the second row
     */
    public static void swapRows(double[][] matrix, int i1, int i2){
        double[] row1 = matrix[i1];
        matrix[i1] = matrix[i2];
        matrix[i2] = row1;
    }

    /**
     * Swaps two columns of a matrix.
     * @param matrix the matrix
     * @param j1 index of the first column
     * @param j2 index of the second column
     */
    public static void swapColumns(double[][] matrix, int j1, int j2){
        for (int i = 0; i < matrix.length; i++){
            double val1 = matrix[i][j1];
            matrix[i][j1] = matrix[i][j2];
            matrix[i][j2] = val1;
        }
    }

    /**
     * Creates a deep copy of the given matrix
     * @param matrix the matrix
     * @returnthe copy of the given matrix
     */
    public double[][] copy(double[][] matrix){
        double[][] newMatrix = new double[matrix.length][];
        for (int i = 0; i < newMatrix.length; i++){
            newMatrix[i] = VectorUtils.copyArray(matrix[i]);
        }
        return newMatrix;
    }

    /**
     * Computes avarage value from all entries iterable the matrix
     * @param matrix the matrix
     * @return avarage value from all entries iterable the matrix
     */
    public static double mean(double[][] matrix){
        double m = 0;
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){
                m += matrix[i][j];
            }
        }
        return m/(matrix.length*matrix[0].length);
    }

    public static double[] means(double[][] matrix) {
        double[] weights = new double[matrix.length];
        Arrays.fill(weights, 1.0);
        return means(matrix, weights);
    }

    public static double[] means(double[][] matrix, double[] weights){
        if (matrix.length == 0){
            return new double[]{};
        }
        double[] retVal = new double[matrix[0].length];
        int rowIndex = 0;
        for (double[] row : matrix) {
            for (int j = 0; j < retVal.length; j++) {
                retVal[j] += weights[rowIndex]*row[j];
            }
            rowIndex++;
        }
        for (int i = 0; i < retVal.length; i++){
            retVal[i] /= VectorUtils.sum(weights);
        }
        return retVal;
    }

    /**
     * Sets a sub-matrix of the given matrix at specified position to values from another smaller matrix
     * @param matrix the matrix
     * @param smallerMatrix the smaller matrix
     * @param startRow the row at which the top row of the smaller matrix should be placed
     * @param startColumn the column at which the left column of the smaller matrix should be placed
     */
    public static void set(double[][] matrix, double[][] smallerMatrix, int startRow, int startColumn){
        for (int i = 0; i < smallerMatrix.length; i++){
            for (int j = 0; j < smallerMatrix[i].length; j++){
                matrix[startRow+i][startColumn+j] = smallerMatrix[i][j];
            }
        }
    }

    /**
     * Extracts a sub-matrix from the given matrix
     * @param matrix the matrix
     * @param startRow the index of the top row of the smaller extracted matrix iterable the bigger matrix
     * @param startColumn the index of the left column of the smaller extracted matrix iterable the bigger matrix
     * @param rowCount number of rows iterable the small matrix
     * @param columnCount number of columns iterable the small matrix
     * @return sub-matrix of the given matrix at the specified position
     */
    public static double[][] submatrix(double[][] matrix, int startRow, int startColumn, int rowCount, int columnCount){
        double[][] retVal = new double[rowCount][columnCount];
        for (int i = startRow; i < startRow+rowCount; i++){
            for (int j = startColumn; j < startColumn+columnCount; j++){
                retVal[i-startRow][j-startColumn] = matrix[i][j];
            }
        }
        return retVal;
    }

    /**
     * Creates a copy of matrix.
     * @param matrix the matrix
     * @return copy of the matrix
     */
    public static double[][] copyMatrix(double[][] matrix){
        double[][] retVal = new double[matrix.length][];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = VectorUtils.copyArray(matrix[i]);
        }
        return retVal;
    }

     /**
     * Creates a copy of matrix.
     * @param matrix the matrix
     * @return copy of the matrix
     */
    public static int[][] copyMatrix(int[][] matrix){
        int[][] retVal = new int[matrix.length][];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = VectorUtils.copyArray(matrix[i]);
        }
        return retVal;
    }

     /**
     * Creates a copy of matrix.
     * @param matrix the matrix
     * @return copy of the matrix
     */
    public static boolean[][] copyMatrix(boolean[][] matrix){
        boolean[][] retVal = new boolean[matrix.length][];
        for (int i = 0; i < retVal.length; i++){
            retVal[i] = VectorUtils.copyArray(matrix[i]);
        }
        return retVal;
    }

    public static boolean gaussianElimination(boolean[][] a, boolean[] b){
        if (a.length == 0){
            return true;
        }
        BitSet[] bs = new BitSet[a.length];
        for (int i = 0; i < a.length; i++){
            bs[i] = new BitSet(a[i].length+1);
        }
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                bs[i].set(j, a[i][j]);
            }
        }
        if (!gaussianElimination(bs, a[0].length)){
            return false;
        }
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                a[i][j] = bs[i].get(j);
            }
            b[i] = bs[i].get(a[i].length);
        }
        return true;
    }

    private static boolean gaussianElimination(BitSet[] system, int length){
        for (int i = 0; i < system.length; i++){
            int col = -1;
            int selected = 0;
            for (int j = i; j < system.length; j++) {
                int c = system[j].nextSetBit(0);
                //0 = 1
                if (c == length){
                    return false;
                }
                if (c != -1 && (c < col || col == -1)) {
                    selected = j;
                    col = c;
                }
            }
            if (col == -1){
                break;
            } else {
                if (selected > i){
                    swap(system, i, selected);
                }
                for (int j = i+1; j < system.length; j++){
                    if (system[j].get(col)){
                        add(system, i, j);
                    }
                }
            }
        }
        for (int i = 0; i < system.length; i++){
            if (!isRowConsistent(system, i, length)){
                return false;
            }
        }
        return true;
    }

    private static void swap(BitSet[] system, int i, int j){
        if (i != j){
            BitSet temp = system[i];
            system[i] = system[j];
            system[j] = temp;
        }
    }

    private static void add(BitSet[] system, int what, int to){
        system[to].xor(system[what]);
    }

    private static boolean isRowConsistent(BitSet[] system, int row, int length){
        int firstOne = system[row].nextSetBit(0);
        //0 = 1
        if (firstOne == length){
            return false;
        }
        //0 = 0
        if (firstOne == -1){
            return true;
        }
        return true;
    }
}
