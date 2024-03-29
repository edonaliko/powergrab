package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class Stateful extends Drone {
    private Feature currentFeature = null;
    private int moves = 0;
    Stateful(Position initialPosition){
        super.currentPosition = initialPosition;
    }

    /**
     * Returns the closest feature given a certain position.
     * @param position Position attribute
     * @return Feature
     */
    private Feature getClosestFeature(Position position){
        double minDistance = Double.MAX_VALUE;
        Feature minFeature = null;
        for(Feature feature: Utils.getFeatures()){
            double distance = findDistance(feature.getPosition(), position);
            if (distance < minDistance && feature.getPower() > 0){
                minDistance = distance;
                minFeature = feature;
            }
        }
        return minFeature;
    }

    public Direction getMove(){
        Feature closest;
        // Checks if there is a feature we are going towards
        if (currentFeature != null){
            closest = currentFeature;
        }
        // If null, it means that we have just collected the previous currentFeature
        else{
            closest = getClosestFeature(currentPosition);
            currentFeature = closest;
        }
        // If the drone gets stuck in a barrier of reds, it will pick a positive feature at random.=
        if (moves >= 20 && closest != null){
            Random random = new Random();
            Feature randomFeature = Utils.getFeatures().get(random.nextInt(Utils.getFeatures().size()));
            while (randomFeature.getPower()<= 0){
                randomFeature = Utils.getFeatures().get(random.nextInt(Utils.getFeatures().size()));
            }
            currentFeature = randomFeature;
            if (randomFeature.getId().equals(closest.getId())){
                currentFeature = null;
            }
            closest = currentFeature;
            moves = 0;
        }
        // If all positive stations have been collected
        if (closest == null){
            for (Direction d: Direction.values()){
                if(nearNegative(currentPosition.nextPosition(d)) && currentPosition.nextPosition(d).inPlayArea()){
                    currentPosition = currentPosition.nextPosition(d);
                    return d;
                }
            }
        }
        assert closest != null;
        double shortest_dist = findDistance(currentPosition, closest.getPosition());
        Direction shortest_dir = null;
        // Finds the direction which puts the drone closest to the selected feature
        for (Direction d : Direction.values()){
            Position nextPosition = currentPosition.nextPosition(d);
            double dist = findDistance(nextPosition, closest.getPosition());
            if (dist < shortest_dist && nearNegative(nextPosition) && nextPosition.inPlayArea()){
                if (dist <= 0.00025 && findLandingFeature(nextPosition) == closest){
                    currentFeature = null;
                    shortest_dir = d;
                    shortest_dist = dist;
                    moves = 0;
                }
                else if (dist > 0.00025){
                    shortest_dir = d;
                    shortest_dist = dist;
                }
            }
        }
        if(shortest_dir == null){
            Direction d = Direction.values()[new Random().nextInt(Direction.values().length)];
            Position nextPosition = currentPosition.nextPosition(d);
            shortest_dir = d;
            while(!nextPosition.inPlayArea() || !nearNegative(nextPosition)){
                d = Direction.values()[new Random().nextInt(Direction.values().length)];
                nextPosition = currentPosition.nextPosition(d);
                if (nearNegative(nextPosition) && nextPosition.inPlayArea()){
                    shortest_dir = d;
                    break;
                }
            }
        }
        moves ++;
        currentPosition = currentPosition.nextPosition(shortest_dir);
        return shortest_dir;
    }
    
}
