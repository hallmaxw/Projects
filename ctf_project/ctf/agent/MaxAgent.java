package ctf.agent;
/*
    BUG: An index out of bounds error occurred in update grid. Not sure what caused it
    TODO: update updateGrid logic to not wipe out FRIENDLY tiles. Should use PreviousState
        to remember location (might cause conflict if the other agent moved into the previous tile).
        Maybe just move back to initial location

    TODO: update the seek out behavior. The agent is very dumb when trying to seek out enemies

*/

import ctf.common.AgentAction;
import ctf.common.AgentEnvironment;

import java.util.*;

public class MaxAgent extends Agent{
    public int moveCount;
    public static TileType[][] grid = new TileType[10][10];
    public Location loc;
    public Location initialLoc;
    public boolean westTeam;
    public PreviousState prevState;

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
        // UPDATE LOCATION
        // REMEMBER:  No obstacles can exist in the far east and west columns
        //            of the board
        updateLocation(environment);
        updateGrid(environment, true);
        System.out.println(moveCount++);



        // booleans describing direction of goal
        // goal is either enemy flag, or our base
        boolean goalNorth;
        boolean goalSouth;
        boolean goalEast;
        boolean goalWest;


        if( !environment.hasFlag(AgentEnvironment.OUR_TEAM) ) {
            int move;
            if(westTeam){
                move = getMoveToLocation(new Location(5, 9), environment.hasFlag());
            }
            else{
                move = getMoveToLocation(new Location(5, 0), environment.hasFlag());
            }
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, move);
            applyActionToLocation(loc, move);
            return move;
        }
        else if(environment.hasFlag()){
            // consider tagging the enemy carrier
            if(environment.hasFlag(AgentEnvironment.ENEMY_TEAM)){
                Location flagLocation = null;
                Location enemyTarget = westTeam ? new Location(5, 9) : new Location(5, 0);
                Location myTarget = westTeam ? new Location(5, 0) : new Location(5, 9);
                int move = 0;
                if(environment.isFlagNorth(AgentEnvironment.OUR_TEAM, true)){
                    flagLocation = new Location(loc.row-1, loc.col);
                    move = AgentAction.MOVE_NORTH;
                }
                else if(environment.isFlagSouth(AgentEnvironment.OUR_TEAM, true)){
                    flagLocation = new Location(loc.row+1, loc.col);
                    move = AgentAction.MOVE_SOUTH;
                }
                else if(environment.isFlagEast(AgentEnvironment.OUR_TEAM, true)){
                    flagLocation = new Location(loc.row, loc.col+1);
                    move = AgentAction.MOVE_EAST;
                }
                else if(environment.isFlagWest(AgentEnvironment.OUR_TEAM, true)){
                    flagLocation = new Location(loc.row, loc.col-1);
                    move = AgentAction.MOVE_WEST;
                }
                if(flagLocation != null){
                    int ourCost = getMovementCost(loc, myTarget);
                    int enemyCost = getMovementCost(flagLocation, enemyTarget);
                    if(enemyCost <= ourCost){
                        // need to tag the carrier
                        System.out.format("Agent: %s TAGGING\n", this);
                        System.out.format("OUR COST: %d ENEMY COST: %d\n", ourCost, enemyCost);
                        applyActionToLocation(loc, move);
                        return move;
                    }
                }
            }
            int move;
            if(westTeam){
                move = getMoveToLocation(new Location(5, 0), environment.hasFlag());
            }
            else{
                move = getMoveToLocation(new Location(5, 9), environment.hasFlag());
            }
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, move);
            applyActionToLocation(loc, move);
            return move;
        }
        else{
            if(!environment.hasFlag(AgentEnvironment.ENEMY_TEAM)){
                // get out of the way
                int move = getMoveToLocation(initialLoc, false);
                applyActionToLocation(loc, move);
                return move;
            }

            // seek out the enemy with the flag
            goalEast = environment.isFlagEast(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseEast(AgentEnvironment.OUR_TEAM, true);
            goalNorth = environment.isFlagNorth(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseNorth(AgentEnvironment.OUR_TEAM, true);
            goalWest = environment.isFlagWest(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseWest(AgentEnvironment.OUR_TEAM, true);
            goalSouth = environment.isFlagSouth(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseSouth(AgentEnvironment.OUR_TEAM, true);

            if(environment.isFlagEast(AgentEnvironment.OUR_TEAM, true)){
                applyActionToLocation(loc, AgentAction.MOVE_EAST);
                return AgentAction.MOVE_EAST;
            }
            if(environment.isFlagNorth(AgentEnvironment.OUR_TEAM, true)){
                applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                return AgentAction.MOVE_NORTH;
            }
            if(environment.isFlagWest(AgentEnvironment.OUR_TEAM, true)){
                applyActionToLocation(loc, AgentAction.MOVE_WEST);
                return AgentAction.MOVE_WEST;
            }

            if(environment.isFlagSouth(AgentEnvironment.OUR_TEAM, true)){
                applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                return AgentAction.MOVE_SOUTH;
            }
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
            applyActionToLocation(loc, AgentAction.MOVE_NORTH);
             return AgentAction.MOVE_NORTH;
        }

        // if goal both north and east
        if( goalNorth && goalEast ) {
             // pick north or east for move with 50/50 chance
             if( Math.random() < 0.5 && !obstNorth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                 return AgentAction.MOVE_NORTH;
             }
             if( !obstEast ) {
                 applyActionToLocation(loc, AgentAction.MOVE_EAST);
                 return AgentAction.MOVE_EAST;
             }
             if( !obstNorth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                 return AgentAction.MOVE_NORTH;
             }
        }

        // if goal both north and west
        if( goalNorth && goalWest ) {
             // pick north or west for move with 50/50 chance
             if( Math.random() < 0.5 && !obstNorth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                 return AgentAction.MOVE_NORTH;
             }
             if( !obstWest ) {
                 applyActionToLocation(loc, AgentAction.MOVE_WEST);
                 return AgentAction.MOVE_WEST;
             }
             if( !obstNorth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                 return AgentAction.MOVE_NORTH;
             }
        }

        // if the goal is south only, and we're not blocked
        if( goalSouth && ! goalEast && ! goalWest && !obstSouth ) {
             // move south
            applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
             return AgentAction.MOVE_SOUTH;
        }

        // do same for southeast and southwest as for north versions
        if( goalSouth && goalEast ) {
             if( Math.random() < 0.5 && !obstSouth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                 return AgentAction.MOVE_SOUTH;
             }
             if( !obstEast ) {
                 applyActionToLocation(loc, AgentAction.MOVE_EAST);
                 return AgentAction.MOVE_EAST;
             }
             if( !obstSouth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                 return AgentAction.MOVE_SOUTH;
             }
        }

        if( goalSouth && goalWest && !obstSouth ) {
             if( Math.random() < 0.5 ) {
                 applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                 return AgentAction.MOVE_SOUTH;
             }
             if( !obstWest ) {
                 applyActionToLocation(loc, AgentAction.MOVE_WEST);
                 return AgentAction.MOVE_WEST;
             }
             if( !obstSouth ) {
                 applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                 return AgentAction.MOVE_SOUTH;
             }
        }

        // if the goal is east only, and we're not blocked
        if( goalEast && !obstEast ) {
            applyActionToLocation(loc, AgentAction.MOVE_EAST);
             return AgentAction.MOVE_EAST;
        }

        // if the goal is west only, and we're not blocked
        if( goalWest && !obstWest ) {
            applyActionToLocation(loc, AgentAction.MOVE_WEST);
             return AgentAction.MOVE_WEST;
        }

        // otherwise, make any unblocked move
        if( !obstNorth ) {
            applyActionToLocation(loc, AgentAction.MOVE_NORTH);
             return AgentAction.MOVE_NORTH;
        }
        else if( !obstSouth ) {
            applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
             return AgentAction.MOVE_SOUTH;
        }
        else if( !obstEast ) {
            applyActionToLocation(loc, AgentAction.MOVE_EAST);
             return AgentAction.MOVE_EAST;
        }
        else if( !obstWest ) {
            applyActionToLocation(loc, AgentAction.MOVE_WEST);
             return AgentAction.MOVE_WEST;
        }
        else {
             // completely blocked!
             applyActionToLocation(loc, AgentAction.DO_NOTHING);
             return AgentAction.DO_NOTHING;
        }
    }

    public void applyActionToLocation(Location loc, int action){
        System.out.format("Agent: %s Row: %d Col: %d Action: %d\n", this, loc.row, loc.col, action);
        switch(action){
            case AgentAction.MOVE_NORTH:
                loc.row--;
                break;
            case AgentAction.MOVE_SOUTH:
                loc.row++;
                break;
            case AgentAction.MOVE_EAST:
                loc.col++;
                break;
            case AgentAction.MOVE_WEST:
                loc.col--;
                break;
        }
    }

    private static TileType GetTileUpdate(TileType tile){

        if(tile == TileType.TEMP_OBSTACLE_10){
            return TileType.TEMP_OBSTACLE_9;
        }
        else if(tile == TileType.TEMP_OBSTACLE_9){
            return TileType.TEMP_OBSTACLE_8;
        }
        else if(tile == TileType.TEMP_OBSTACLE_8){
            return TileType.TEMP_OBSTACLE_7;
        }
        else if(tile == TileType.TEMP_OBSTACLE_7){
            return TileType.TEMP_OBSTACLE_6;
        }
        else if(tile == TileType.TEMP_OBSTACLE_6){
            return TileType.TEMP_OBSTACLE_5;
        }
        else if(tile == TileType.TEMP_OBSTACLE_5){
            return TileType.TEMP_OBSTACLE_4;
        }
        else if(tile == TileType.TEMP_OBSTACLE_4){
            return TileType.TEMP_OBSTACLE_3;
        }
        else if(tile == TileType.TEMP_OBSTACLE_3){
            return TileType.TEMP_OBSTACLE_2;
        }
        else if(tile == TileType.TEMP_OBSTACLE_2){
            return TileType.TEMP_OBSTACLE_1;
        }
        else if(tile == TileType.TEMP_OBSTACLE_1){
            return TileType.EMPTY;
        }
        return tile;
    }

    public void updateGrid(AgentEnvironment environment, boolean includeEnemies){
        // update tiles
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                grid[row][col] = GetTileUpdate(grid[row][col]);
            }
        }
        grid[prevState.loc.row][prevState.loc.col] = TileType.EMPTY;
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

        // these could override the base tiles. Always set them back to base
        if(westTeam){
            grid[5][0] = TileType.OUR_BASE;
            grid[5][9] = TileType.ENEMY_BASE;
        }
        else{
            grid[5][0] = TileType.ENEMY_BASE;
            grid[5][9] = TileType.OUR_BASE;
        }

        if(loc.col < 9 && environment.isAgentEast(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row][loc.col+1] = TileType.FRIENDLY;
        if(loc.col > 0 && environment.isAgentWest(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row][loc.col-1] = TileType.FRIENDLY;
        if(loc.row > 0 && environment.isAgentNorth(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row-1][loc.col] = TileType.FRIENDLY;
        if(loc.row < 9 && environment.isAgentSouth(AgentEnvironment.OUR_TEAM, true))
            grid[loc.row+1][loc.col] = TileType.FRIENDLY;

        if(includeEnemies){
            if(loc.col < 9 && environment.isAgentEast(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row][loc.col+1] = TileType.TEMP_OBSTACLE_10;
            if(loc.col > 0 && environment.isAgentWest(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row][loc.col-1] = TileType.TEMP_OBSTACLE_10;
            if(loc.row > 0 && environment.isAgentNorth(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row-1][loc.col] = TileType.TEMP_OBSTACLE_10;
            if(loc.row < 9 && environment.isAgentSouth(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row+1][loc.col] = TileType.TEMP_OBSTACLE_10;
        }
    }

    /*
      Check if the Agent's location was reset. If so, update location.
    */
    public void updateLocation(AgentEnvironment environment){
        if(environment.isBaseEast(AgentEnvironment.OUR_TEAM, false) || environment.isBaseWest(AgentEnvironment.OUR_TEAM, false))
            return;

        // the agent is on it's edge of the grid
        if(environment.isObstacleNorthImmediate()){
            System.out.format("%s: RESET OCCURRED\n", this);
            // agent at top corner
            loc.row = 0;
            if(westTeam){
                loc.col = 0;
            }
            else{
                loc.col = 9;
            }
        }
        else if(environment.isObstacleSouthImmediate()){
            System.out.format("%s: RESET OCCURRED\n", this);
            // agent at bottom corner
            loc.row = 9;
            if(westTeam){
                loc.col = 0;
            }
            else{
                loc.col = 9;
            }
        }
    }

    public int getMovementCost(final Location loc, final Location newLoc){
        HashSet<Location> checkedLocs = new HashSet<Location>();
        PriorityQueue<SearchLocation> queue = new PriorityQueue<SearchLocation>(10, new Comparator<SearchLocation>() {
            @Override
            public int compare(SearchLocation o1, SearchLocation o2) {
                int cost1 = newLoc.getDistance(o1.loc) + o1.cost;
                int cost2 = newLoc.getDistance(o2.loc) + o2.cost;
                return Integer.compare(cost1, cost2);
            }
        });
        if(newLoc.equals(loc))
            return 0;
        if (CanMoveNorth(loc, true))
            queue.offer(new SearchLocation(new Location(loc.row - 1, loc.col), AgentAction.MOVE_NORTH, 1));
        if (CanMoveSouth(loc, true))
            queue.offer(new SearchLocation(new Location(loc.row + 1, loc.col), AgentAction.MOVE_SOUTH, 1));
        if (CanMoveEast(loc, true))
            queue.offer(new SearchLocation(new Location(loc.row, loc.col + 1), AgentAction.MOVE_EAST, 1));
        if (CanMoveWest(loc, true))
            queue.offer(new SearchLocation(new Location(loc.row, loc.col - 1), AgentAction.MOVE_WEST, 1));
        checkedLocs.add(loc);
        while (!queue.isEmpty()) {
            SearchLocation currentLoc = queue.poll();
            //System.out.format("Attempt: %s Goal: %s\n", currentLoc.loc, newLoc);
            if (currentLoc.loc.equals(newLoc)) {
                // success
                return currentLoc.cost;
            }
            checkedLocs.add(currentLoc.loc);
            for (SearchLocation tempLoc : currentLoc.getSuccessors(true)) {
                if (checkedLocs.contains(tempLoc.loc)) {
                    //System.out.format("%s discarded\n", tempLoc.loc);
                    continue;
                }
                checkedLocs.add(tempLoc.loc);
                queue.offer(tempLoc);
            }
        }
        return Integer.MAX_VALUE;
    }

    public int getMoveToLocation(final Location newLoc, boolean hasFlag) {
        HashSet<Location> checkedLocs = new HashSet<Location>();
        PriorityQueue<SearchLocation> queue = new PriorityQueue<SearchLocation>(10, new Comparator<SearchLocation>() {
            @Override
            public int compare(SearchLocation o1, SearchLocation o2) {
                int cost1 = newLoc.getDistance(o1.loc) + o1.cost;
                int cost2 = newLoc.getDistance(o2.loc) + o2.cost;
                return Integer.compare(cost1, cost2);
            }
        });
        if(newLoc.equals(loc))
            return AgentAction.DO_NOTHING;
        if (CanMoveNorth(loc, hasFlag))
            queue.offer(new SearchLocation(new Location(loc.row - 1, loc.col), AgentAction.MOVE_NORTH, 1));
        if (CanMoveSouth(loc, hasFlag))
            queue.offer(new SearchLocation(new Location(loc.row + 1, loc.col), AgentAction.MOVE_SOUTH, 1));
        if (CanMoveEast(loc, hasFlag))
            queue.offer(new SearchLocation(new Location(loc.row, loc.col + 1), AgentAction.MOVE_EAST, 1));
        if (CanMoveWest(loc, hasFlag))
            queue.offer(new SearchLocation(new Location(loc.row, loc.col - 1), AgentAction.MOVE_WEST, 1));
        checkedLocs.add(loc);
        while (!queue.isEmpty()) {
            SearchLocation currentLoc = queue.poll();
            //System.out.format("Attempt: %s Goal: %s\n", currentLoc.loc, newLoc);
            if (currentLoc.loc.equals(newLoc)) {
                // success
                return currentLoc.action;
            }
            checkedLocs.add(currentLoc.loc);
            for (SearchLocation tempLoc : currentLoc.getSuccessors(hasFlag)) {
                if (checkedLocs.contains(tempLoc.loc)) {
                    //System.out.format("%s discarded\n", tempLoc.loc);
                    continue;
                }
                checkedLocs.add(tempLoc.loc);
                queue.offer(tempLoc);
            }
        }
        return AgentAction.DO_NOTHING;
    }

    public static boolean CanMoveNorth(Location loc, boolean hasFlag){
        if(loc.row > 0 && (grid[loc.row-1][loc.col] == TileType.EMPTY || grid[loc.row-1][loc.col] == TileType.ENEMY_BASE
                || (grid[loc.row-1][loc.col] == TileType.OUR_BASE && hasFlag) ||
                grid[loc.row-1][loc.col] == TileType.UNKNOWN))
            return true;
        return false;
    }

    public static boolean CanMoveSouth(Location loc, boolean hasFlag){
        if(loc.row < 9 && (grid[loc.row+1][loc.col] == TileType.EMPTY || grid[loc.row+1][loc.col] == TileType.ENEMY_BASE
                || (grid[loc.row+1][loc.col] == TileType.OUR_BASE && hasFlag) ||
                grid[loc.row+1][loc.col] == TileType.UNKNOWN))
            return true;
        return false;
    }

    public static boolean CanMoveEast(Location loc, boolean hasFlag){
        if(loc.col < 9 && (grid[loc.row][loc.col+1] == TileType.EMPTY || grid[loc.row][loc.col+1] == TileType.ENEMY_BASE
                || (grid[loc.row][loc.col+1] == TileType.OUR_BASE && hasFlag) ||
                grid[loc.row][loc.col+1] == TileType.UNKNOWN)){
            return true;
        }
        return false;
    }

    public static boolean CanMoveWest(Location loc, boolean hasFlag){
        if(loc.col > 0 && (grid[loc.row][loc.col-1] == TileType.EMPTY || grid[loc.row][loc.col-1] == TileType.ENEMY_BASE
                || (grid[loc.row][loc.col-1] == TileType.OUR_BASE && hasFlag) ||
                grid[loc.row][loc.col-1] == TileType.UNKNOWN))
            return true;
        return false;
    }

    public class PreviousState{
        public Location loc;
        public AgentEnvironment environment;
        public int action;

        public PreviousState(Location loc, AgentEnvironment environment, int action){
            this.loc = loc;
            this.environment = environment;
            this.action = action;
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

        public ArrayList<SearchLocation> getSuccessors(boolean hasFlag){
            ArrayList<SearchLocation> successors = new ArrayList<SearchLocation>();
            if(CanMoveNorth(loc, hasFlag))
                successors.add(new SearchLocation(new Location(loc.row - 1, loc.col), action, cost+1));
            if(CanMoveSouth(loc, hasFlag))
                successors.add(new SearchLocation(new Location(loc.row + 1, loc.col), action, cost+1));
            if(CanMoveEast(loc, hasFlag))
                successors.add(new SearchLocation(new Location(loc.row, loc.col + 1), action, cost+1));
            if(CanMoveWest(loc, hasFlag))
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
        UNKNOWN, EMPTY, OBSTACLE, ENEMY_BASE, OUR_BASE, FRIENDLY,
        TEMP_OBSTACLE_10, TEMP_OBSTACLE_9, TEMP_OBSTACLE_8, TEMP_OBSTACLE_7,
        TEMP_OBSTACLE_6, TEMP_OBSTACLE_5, TEMP_OBSTACLE_4, TEMP_OBSTACLE_3,
        TEMP_OBSTACLE_2, TEMP_OBSTACLE_1
    }
}
