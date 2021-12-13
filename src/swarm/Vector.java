package swarm;

public class Vector {
    double[] vectorPoints;

    public Vector(double[] vectorPoints) {
        this.vectorPoints = vectorPoints;
    }

    public Vector(double[] point1, double[] point2) {
        if (point1.length != point2.length)
            throw new IllegalArgumentException("Number of axis don't match");

        vectorPoints = new double[point1.length];

        for (int i = 0; i < point1.length; i++) {
            vectorPoints[i] = point2[i] - point1[i];
        }
    }

    public void updateVector(double[] vectorPoints) {
        if (this.vectorPoints.length != vectorPoints.length)
            throw new IllegalArgumentException("The number of axis in the given vector do not match the number of axis in this vector.");

        this.vectorPoints = vectorPoints;
    }

    public int getNumberOfAxis() {
        return vectorPoints.length;
    }

    public double[] getVectorPoints() {
        return vectorPoints;
    }
}