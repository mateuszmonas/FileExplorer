package main;

public class MousePosition {

    static final MousePosition ZERO = new MousePosition(0,0);

    private double x;
    private double y;

    void setPosition(double x, double y){
        this.x=x;
        this.y=y;
    }

    private MousePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    boolean equals(double x, double y){
        return this.x==x && this.y==y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MousePosition && ((MousePosition) obj).x==x && ((MousePosition) obj).y==y;
    }
}
