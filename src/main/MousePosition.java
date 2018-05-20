package main;

public class MousePosition {

    double x;
    double y;

    void set(double x, double y){
        this.x=x;
        this.y=y;
    }

    boolean equals(double x, double y){
        return this.x==x && this.y==y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MousePosition && ((MousePosition) obj).x==x && ((MousePosition) obj).y==y;
    }
}
