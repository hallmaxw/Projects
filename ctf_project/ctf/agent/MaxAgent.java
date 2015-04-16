package ctf.agent;


import ctf.common.AgentAction;
import ctf.common.AgentEnvironment;

import java.util.*;

public class MaxAgent extends Agent{
    public int moveCount;
    public static TileType[][] grid = new TileType[10][10];
    public Location loc;
    public Location initialLoc;
    public boolean westTeam;

    public MaxAgent(){
        moveCount = 0;
        loc = new Location(0, 0);
        initialLoc = new Location(0, 0);
        westTeam = false;
        for(int i = 0; i < 10; i++){
            for(int k = 0; k < 10; k++){
                grid[i][k] = TileType.UNKNOWN;
            }
        }
    }

    public void updateGrid(AgentEnvironment environment){
        if(loc.col < 9 && environment.isObstacleEastImmediate())
            grid[loc.row][loc.col+1] = TileType.OBSTACLE;
        if(loc.col > 0 && environment.isObstacleWestImmediate())
            grid[loc.row][loc.col-1] = TileType.OBSTACLE;
        if(loc.row > 0 && environment.isObstacleNorthImmediate())
            grid[loc.row-1][loc.col] = TileType.OBSTACLE;
        if(loc.row < 9 && environment.isObstacleSouthImmediate())
            grid[loc.row+1][loc.col] = TileType.OBSTACLE;

        if(loc.col < 9 && !environment.isObstacleEastImmediate())
            grid[loc.row][loc.col+1] = TileType.EMPTY;
        if(loc.col > 0 && !environment.isObstacleWestImmediate())
            grid[loc.row][loc.col-1] = TileType.EMPTY;
        if(loc.row > 0 && !environment.isObstacleNorthImmediate())
            grid[loc.row-1][loc.col] = TileType.EMPTY;
        if(loc.row < 9 && !environment.isObstacleSouthImmediate())
            grid[loc.row+1][loc.col] = TileType.EMPTY;

        if(loc.col < 9 && environment.isAgentEast(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row][loc.col+1] = TileType.FRIENDLY;
        if(loc.col > 0 && environment.isAgentWest(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row][loc.col-1] = TileType.FRIENDLY;
        if(loc.row > 0 && environment.isAgentNorth(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row-1][loc.col] = TileType.FRIENDLY;
        if(loc.row < 9 && environment.isAgentSouth(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row+1][loc.col] = TileType.FRIENDLY;
    }

    public int getMoveToLocation(final Location newLoc){
        HashSet<Location> checkedLocs = new HashSet<Location>();
        PriorityQueue<SearchLocation> queue = new PriorityQueue<SearchLocation>(10, new Comparator<SearchLocation>() {
            @Override
            public int compare(SearchLocation o1, SearchLocation o2) {
                int cost1 = newLoc.getDistance(o1.loc) + o1.cost;
                int cost2 = newLoc.getDistance(o2.loc) + o2.cost;
                return Integer.compare(cost1, cost2);
            }
        });
        if(CanMoveNorth(loc))
            queue.offer(new SearchLocation(new Location(loc.row-1, loc.col), AgentAction.MOVE_NORTH, 1));
        if(CanMoveSouth(loc))
            queue.offer(new SearchLocation(new Location(loc.row+1, loc.col), AgentAction.MOVE_SOUTH, 1));
        if(CanMoveEast(loc))
            queue.offer(new SearchLocation(new Location(loc.row, loc.col+1), AgentAction.MOVE_EAST, 1));
        if(CanMoveWest(loc))
            queue.offer(new SearchLocation(new Location(loc.row, loc.col-1), AgentAction.MOVE_WEST, 1));

        while(!queue.isEmpty()){
//            Iterator<SearchLocation> it = queue.iterator();
//            System.out.println("CONTENTS");
//            while(it.hasNext())
//                System.out.println(it.next().loc);
            SearchLocation currentLoc = queue.poll();
            //System.out.format("Attempt: %s Goal: %s\n", currentLoc.loc, newLoc);
            if(currentLoc.loc.equals(newLoc)){
                // success
                return currentLoc.action;
            }
            checkedLocs.add(currentLoc.loc);
            for(SearchLocation tempLoc : currentLoc.getSuccessors()){
                if(checkedLocs.contains(tempLoc.loc))
                    continue;
                queue.offer(tempLoc);
            }
        }
        return AgentAction.DO_NOTHING;
    }

    public static boolean CanMoveNorth(Location loc){
        if(loc.row > 0 && grid[loc.row-1][loc.col] != TileType.OBSTACLE && grid[loc.row-1][loc.col] != TileType.FRIENDLY)
            return true;
        return false;
    }

    public static boolean CanMoveSouth(Location loc){
        if(loc.row < 9 && grid[loc.row+1][loc.col] != TileType.OBSTACLE && grid[loc.row+1][loc.col] != TileType.FRIENDLY)
            return true;
        return false;
    }

    public static boolean CanMoveEast(Location loc){
        if(loc.col < 9 && grid[loc.row][loc.col+1] != TileType.OBSTACLE && grid[loc.row][loc.col+1] != TileType.FRIENDLY){
            //System.out.format("EAST TEST SUCCESS: %s\n", loc);
            return true;
        }
        //System.out.format("EAST TEST FAIL: %s\n", loc);
        return false;
    }

    public static boolean CanMoveWest(Location loc){
        if(loc.col > 0 && grid[loc.row][loc.col-1] != TileType.OBSTACLE && grid[loc.row][loc.col-1] != TileType.FRIENDLY)
            return true;
        return false;
    }
    // implements Agent.getMove() interface
    public int getMove( AgentEnvironment environment ) {
        // get initial location info
        if(moveCount == 0){

            if(environment.isBaseSouth(AgentEnvironment.OUR_TEAM, false)){
                // top agent
                loc.row = 0;
                initialLoc.row = 0;
            }
            else{
                // bottom agent
                loc.row = 9;
                initialLoc.row = 9;
            }
            if(environment.isBaseEast(AgentEnvironment.ENEMY_TEAM, false)){
                // west team
                loc.col = 0;
                initialLoc.col = 0;
                grid[5][0] = TileType.OUR_BASE;
                grid[5][9] = TileType.ENEMY_BASE;
                westTeam = true;
            }
            else{
                westTeam = false;
                loc.col = 9;
                initialLoc.col = 9;
                grid[5][0] = TileType.ENEMY_BASE;
                grid[5][9] = TileType.OUR_BASE;
            }
        }
        updateGrid(environment);
        System.out.println(moveCount++);

        // booleans describing direction of goal
        // goal is either enemy flag, or our base
        boolean goalNorth;
        boolean goalSouth;
        boolean goalEast;
        boolean goalWest;


        if( !environment.hasFlag() ) {
            int move;
            if(westTeam){
                move = getMoveToLocation(new Location(5, 9));
            }
            else{
                move = getMoveToLocation(new Location(5, 0));
            }
            System.out.format("Agent: %s Row: %d Col: %d Action: %d\n", this, loc.row, loc.col, move);
            switch(move){
                case AgentAction.MOVE_EAST:
                    loc.col++;
                    break;
                case AgentAction.MOVE_NORTH:
                    loc.row--;
                    break;
                case AgentAction.MOVE_SOUTH:
                    loc.row++;
                    break;
                case AgentAction.MOVE_WEST:
                    loc.col--;
                    break;
            }
            return move;
//            // make goal the enemy flag
//            goalNorth = environment.isFlagNorth(
//                    environment.ENEMY_TEAM, false );
//
//            goalSouth = environment.isFlagSouth(
//                    environment.ENEMY_TEAM, false );
//
//            goalEast = environment.isFlagEast(
//                    environment.ENEMY_TEAM, false );
//
//            goalWest = environment.isFlagWest(
//                    environment.ENEMY_TEAM, false );
        }
        else {
            // we have enemy flag.
            // make goal our base
            goalNorth = environment.isBaseNorth(
                    environment.OUR_TEAM, false );

            goalSouth = environment.isBaseSouth(
                    environment.OUR_TEAM, false );

            goalEast = environment.isBaseEast(
                    environment.OUR_TEAM, false );

            goalWest = environment.isBaseWest(
                    environment.OUR_TEAM, false );
        }

        // now we have direction booleans for our goal

        // check for immediate obstacles blocking our path
        boolean obstNorth = environment.isObstacleNorthImmediate();
        boolean obstSouth = environment.isObstacleSouthImmediate();
        boolean obstEast = environment.isObstacleEastImmediate();
        boolean obstWest = environment.isObstacleWestImmediate();


        // if the goal is north only, and we're not blocked
        if( goalNorth && ! goalEast && ! goalWest && !obstNorth ) {
            // move north
            return AgentAction.MOVE_NORTH;
        }

        // if goal both north and east
        if( goalNorth && goalEast ) {
            // pick north or east for move with 50/50 chance
            if( Math.random() < 0.5 && !obstNorth ) {
                return AgentAction.MOVE_NORTH;
            }
            if( !obstEast ) {
                return AgentAction.MOVE_EAST;
            }
            if( !obstNorth ) {
                return AgentAction.MOVE_NORTH;
            }
        }

        // if goal both north and west
        if( goalNorth && goalWest ) {
            // pick north or west for move with 50/50 chance
            if( Math.random() < 0.5 && !obstNorth ) {
                return AgentAction.MOVE_NORTH;
            }
            if( !obstWest ) {
                return AgentAction.MOVE_WEST;
            }
            if( !obstNorth ) {
                return AgentAction.MOVE_NORTH;
            }
        }

        // if the goal is south only, and we're not blocked
        if( goalSouth && ! goalEast && ! goalWest && !obstSouth ) {
            // move south
            return AgentAction.MOVE_SOUTH;
        }

        // do same for southeast and southwest as for north versions
        if( goalSouth && goalEast ) {
            if( Math.random() < 0.5 && !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
            }
            if( !obstEast ) {
                return AgentAction.MOVE_EAST;
            }
            if( !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
            }
        }

        if( goalSouth && goalWest && !obstSouth ) {
            if( Math.random() < 0.5 ) {
                return AgentAction.MOVE_SOUTH;
            }
            if( !obstWest ) {
                return AgentAction.MOVE_WEST;
            }
            if( !obstSouth ) {
                return AgentAction.MOVE_SOUTH;
            }
        }

        // if the goal is east only, and we're not blocked
        if( goalEast && !obstEast ) {
            return AgentAction.MOVE_EAST;
        }

        // if the goal is west only, and we're not blocked
        if( goalWest && !obstWest ) {
            return AgentAction.MOVE_WEST;
        }

        // otherwise, make any unblocked move
        if( !obstNorth ) {
            return AgentAction.MOVE_NORTH;
        }
        else if( !obstSouth ) {
            return AgentAction.MOVE_SOUTH;
        }
        else if( !obstEast ) {
            return AgentAction.MOVE_EAST;
        }
        else if( !obstWest ) {
            return AgentAction.MOVE_WEST;
        }
        else {
            // completely blocked!
            return AgentAction.DO_NOTHING;
        }
    }
    public class SearchLocation{
        public Location loc;
        public int action;
        public int cost;

        public SearchLocation(Location loc, int action, int cost){
            this.loc = loc;
            this.action = action;
            this.cost = cost;
        }

        public ArrayList<SearchLocation> getSuccessors(){
            ArrayList<SearchLocation> successors = new ArrayList<SearchLocation>();
            if(CanMoveNorth(loc))
                successors.add(new SearchLocation(new Location(loc.row - 1, loc.col), action, cost+1));
            if(CanMoveSouth(loc))
                successors.add(new SearchLocation(new Location(loc.row + 1, loc.col), action, cost+1));
            if(CanMoveEast(loc))
                successors.add(new SearchLocation(new Location(loc.row, loc.col + 1), action, cost+1));
            if(CanMoveWest(loc))
                successors.add(new SearchLocation(new Location(loc.row, loc.col - 1), action, cost + 1));
            return successors;
        }
    }
    public class Location{
        public int row;
        public int col;

        Location(int row, int col){
            this.row = row;
            this.col = col;
        }

        public int getDistance(Location loc){
            return Math.abs(loc.row-row) + Math.abs(loc.col-col);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Location location = (Location) o;

            if (col != location.col) return false;
            if (row != location.row) return false;

            return true;
        }

        public String toString(){
            return String.format("Row: %d Col: %d", row, col);
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }
    }

    public enum TileType{
        UNKNOWN, EMPTY, OBSTACLE, ENEMY_BASE, OUR_BASE, FRIENDLY
    }
}