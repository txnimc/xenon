package me.jellysquid.mods.sodium.client.util;

import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.Dim2iExtended;
import me.jellysquid.mods.sodium.client.gui.reesesoptions.client.gui.Point2i;

import java.util.Objects;

public final class Dim2i implements Dim2iExtended, Point2i
{
    private Point2i point2i;
    private int x;
    private int y;
    private int width;
    private int height;

    public Dim2i(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getLimitX()
    {
        return (this.x() + this.width());
    }

    public int getLimitY()
    {
        return (this.y() + this.height());
    }

    public boolean containsCursor(double x, double y)
    {
        return (x >= (double) this.x() && x < (double) this.getLimitX() && y >= (double) this.y() && y < (double) this.getLimitY());
    }

    public int getCenterX()
    {
        return (this.x() + this.width() / 2);
    }

    public int getCenterY()
    {
        return (this.y() + this.height() / 2);
    }

    public Point2i point2i()
    {
        return point2i;
    }

    public int x()
    {
        if (this.point2i != null) {
            return (this.x + this.point2i.getX());
        }

        return x;
    }

    public int y()
    {
        if (this.point2i != null) {
            return (this.y + this.point2i.getY());
        }

        return y;
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Dim2i) obj;
        return Objects.equals(this.point2i, that.point2i) &&
                this.x == that.x &&
                this.y == that.y &&
                this.width == that.width &&
                this.height == that.height;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(point2i, x, y, width, height);
    }

    @Override
    public String toString()
    {
        return "Dim2i[" +
                "point2i=" + point2i + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "width=" + width + ", " +
                "height=" + height + ']';
    }

    @Override
    public void setPoint2i(Point2i point2i) {
        this.point2i = point2i;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getX() {
        return this.x();
    }

    @Override
    public int getY() {
        return this.y();
    }

    @Override
    public boolean canFitDimension(Dim2i anotherDim) {
        return this.x() <= anotherDim.x() && this.y() <= anotherDim.y() && this.getLimitX() >= anotherDim.getLimitX() && this.getLimitY() >= anotherDim.getLimitY();
    }

    @Override
    public boolean overlapWith(Dim2i other) {
        return this.x() < other.getLimitX() && this.getLimitX() > other.x() && this.y() < other.getLimitY() && this.getLimitY() > other.y();
    }

    public Dim2i withHeight(int newHeight) {
        return new Dim2i(x, y, width, newHeight);
    }

    public Dim2i withWidth(int newWidth) {
        return new Dim2i(x, y, newWidth, height);
    }

    public Dim2i withX(int newX) {
        return new Dim2i(newX, y, width, height);
    }

    public Dim2i withY(int newY) {
        return new Dim2i(x, newY, width, height);
    }

    public boolean overlapsWith(Dim2i other) {
        return this.x() < other.getLimitX() && this.getLimitX() > other.x() && this.y() < other.getLimitY() && this.getLimitY() > other.y();
    }

    public Dim2i withParentOffset(Point2i parent) {
        return new Dim2i(parent.getX() + x, parent.getY() + y, width, height);
    }
}
