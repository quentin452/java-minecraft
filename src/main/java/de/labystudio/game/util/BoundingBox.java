package de.labystudio.game.util;

import java.util.Objects;

public class BoundingBox {

    private final double epsilon = 0.0F;

    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    /**
     * Bounding box
     *
     * @param minX Minimum x side
     * @param minY Minimum y side
     * @param minZ Minimum z side
     * @param maxX Maximum x side
     * @param maxY Maximum y side
     * @param maxZ Maximum z side
     */
    public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Copy the current bounding box object
     *
     * @return Clone of the bounding box
     */
    public BoundingBox clone() {
        return new BoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    /**
     * Expand the bounding box. Positive and negative numbers controls which side of the box should grow.
     *
     * @param x Amount to expand the minX or maxX
     * @param y Amount to expand the minY or maxY
     * @param z Amount to expand the minZ or maxZ
     * @return The expanded bounding box
     */
    public BoundingBox expand(double x, double y, double z) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;

        // Handle expanding of min/max x
        if (x < 0.0F) {
            minX += x;
        } else {
            maxX += x;
        }

        // Handle expanding of min/max y
        if (y < 0.0F) {
            minY += y;
        } else {
            maxY += y;
        }

        // Handle expanding of min/max z
        if (z < 0.0F) {
            minZ += z;
        } else {
            maxZ += z;
        }

        // Create new bounding box
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Expand the bounding box on both sides.
     * The center is always fixed when using grow.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public BoundingBox grow(double x, double y, double z) {
        return new BoundingBox(this.minX - x, this.minY - y,
                this.minZ - z, this.maxX + x,
                this.maxY + y, this.maxZ + z);
    }

    /**
     * Check for collision on the X axis
     *
     * @param otherBoundingBox The other bounding box that is colliding with the this one.
     * @param x                Position on the X axis that is colliding
     * @return Returns the corrected x position that collided.
     */
    public double clipXCollide(BoundingBox otherBoundingBox, double x) {
        // Check if the boxes are colliding on the Y axis
        if (otherBoundingBox.maxY <= this.minY || otherBoundingBox.minY >= this.maxY) {
            return x;
        }

        // Check if the boxes are colliding on the Z axis
        if (otherBoundingBox.maxZ <= this.minZ || otherBoundingBox.minZ >= this.maxZ) {
            return x;
        }

        // Check for collision if the X axis of the current box is bigger
        if (x > 0.0F && otherBoundingBox.maxX <= this.minX) {
            double max = this.minX - otherBoundingBox.maxX - this.epsilon;
            if (max < x) {
                x = max;
            }
        }

        // Check for collision if the X axis of the current box is smaller
        if (x < 0.0F && otherBoundingBox.minX >= this.maxX) {
            double max = this.maxX - otherBoundingBox.minX + this.epsilon;
            if (max > x) {
                x = max;
            }
        }

        return x;
    }

    /**
     * Check for collision on the Y axis
     *
     * @param otherBoundingBox The other bounding box that is colliding with the this one.
     * @param y                Position on the X axis that is colliding
     * @return Returns the corrected x position that collided.
     */
    public double clipYCollide(BoundingBox otherBoundingBox, double y) {
        // Check if the boxes are colliding on the X axis
        if (otherBoundingBox.maxX <= this.minX || otherBoundingBox.minX >= this.maxX) {
            return y;
        }

        // Check if the boxes are colliding on the Z axis
        if (otherBoundingBox.maxZ <= this.minZ || otherBoundingBox.minZ >= this.maxZ) {
            return y;
        }

        // Check for collision if the Y axis of the current box is bigger
        if (y > 0.0F && otherBoundingBox.maxY <= this.minY) {
            double max = this.minY - otherBoundingBox.maxY - this.epsilon;
            if (max < y) {
                y = max;
            }
        }

        // Check for collision if the Y axis of the current box is bigger
        if (y < 0.0F && otherBoundingBox.minY >= this.maxY) {
            double max = this.maxY - otherBoundingBox.minY + this.epsilon;
            if (max > y) {
                y = max;
            }
        }

        return y;
    }

    /**
     * Check for collision on the Y axis
     *
     * @param otherBoundingBox The other bounding box that is colliding with the this one.
     * @param z                Position on the X axis that is colliding
     * @return Returns the corrected x position that collided.
     */
    public double clipZCollide(BoundingBox otherBoundingBox, double z) {
        // Check if the boxes are colliding on the X axis
        if (otherBoundingBox.maxX <= this.minX || otherBoundingBox.minX >= this.maxX) {
            return z;
        }

        // Check if the boxes are colliding on the Y axis
        if (otherBoundingBox.maxY <= this.minY || otherBoundingBox.minY >= this.maxY) {
            return z;
        }

        // Check for collision if the Z axis of the current box is bigger
        if (z > 0.0F && otherBoundingBox.maxZ <= this.minZ) {
            double max = this.minZ - otherBoundingBox.maxZ - this.epsilon;
            if (max < z) {
                z = max;
            }
        }

        // Check for collision if the Z axis of the current box is bigger
        if (z < 0.0F && otherBoundingBox.minZ >= this.maxZ) {
            double max = this.maxZ - otherBoundingBox.minZ + this.epsilon;
            if (max > z) {
                z = max;
            }
        }

        return z;
    }

    /**
     * Check if the two boxes are intersecting/overlapping
     *
     * @param otherBoundingBox The other bounding box that could intersect
     * @return The two boxes are overlapping
     */
    public boolean intersects(BoundingBox otherBoundingBox) {
        // Check on X axis
        if (otherBoundingBox.maxX <= this.minX || otherBoundingBox.minX >= this.maxX) {
            return false;
        }

        // Check on Y axis
        if (otherBoundingBox.maxY <= this.minY || otherBoundingBox.minY >= this.maxY) {
            return false;
        }

        // Check on Z axis
        return (!(otherBoundingBox.maxZ <= this.minZ)) && (!(otherBoundingBox.minZ >= this.maxZ));
    }

    /**
     * Move the bounding box relative.
     *
     * @param x Relative offset x
     * @param y Relative offset y
     * @param z Relative offset z
     */
    public void move(double x, double y, double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
    }

    /**
     * Create a new bounding box with the given offset
     *
     * @param x Relative offset x
     * @param y Relative offset x
     * @param z Relative offset x
     * @return New bounding box with the given offset relative to this bounding box
     */
    public BoundingBox offset(double x, double y, double z) {
        return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BoundingBox that = (BoundingBox) obj;
        return Double.compare(that.minX, minX) == 0 &&
                Double.compare(that.minY, minY) == 0 &&
                Double.compare(that.minZ, minZ) == 0 &&
                Double.compare(that.maxX, maxX) == 0 &&
                Double.compare(that.maxY, maxY) == 0 &&
                Double.compare(that.maxZ, maxZ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
