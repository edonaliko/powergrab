package uk.ac.ed.inf;

public enum Direction {

    N(90), NNE(67.5), NE(45), ENE(22.5), E(0), ESE(337.5), SE(315), SSE(292.5), S(270), SSW(247.5), SW(225), WSW(202.53), W(180), WNW(157.5), NW(135), NNW(112.5);

    public double angle;

    Direction(double angle) {
        this.angle = angle;
    }
}
