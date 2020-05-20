package comG.lab4;

public class TwoDTree {
    TwoDTree left;
    TwoDTree right;
    TwoDTree parent;
    private int value;
    private boolean verticalSplitting;

    public TwoDTree(){}

    public TwoDTree(TwoDTree parent, boolean verticalSplitting) {
        this.parent = parent;
        this.verticalSplitting = verticalSplitting;
    }

    public void setVerticalSplitting(boolean verticalSplitting) {
        this.verticalSplitting = verticalSplitting;
    }

    public boolean isVerticalSplitting() {
        return verticalSplitting;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}