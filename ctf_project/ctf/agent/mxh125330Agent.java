package ctf.agent;

/*
    Author: Maxwell Hall
*/

import ctf.common.AgentAction;
import ctf.common.AgentEnvironment;
import java.lang.Math;
import java.util.*;

public class mxh125330Agent extends Agent{
    public static boolean DEBUG = false;
    public int moveCount;
    public static TileType[][] grid = new TileType[10][10];
    public Location loc;
    public Location initialLoc;
    public boolean westTeam;
    public PreviousState prevState;
    /*
      Temporary obstacles should reset on average after 15 moves (both agents'
      moves count)
    */
    public static final double TILE_RESET_PROBABILITY = 1/((double)15);


    public mxh125330Agent(){
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


    public void setInitialInformation(AgentEnvironment environment){
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
          // east team
          westTeam = false;
          loc.col = 9;
          initialLoc.col = 9;
          grid[5][0] = TileType.ENEMY_BASE;
          grid[5][9] = TileType.OUR_BASE;
      }
    }


    public int getMove( AgentEnvironment environment ) {
        // get initial location info
        if(moveCount == 0){
            setInitialInformation(environment);
        }
        // UPDATE LOCATION
        // REMEMBER:  No obstacles can exist in the far east and west columns
        //            of the board
        updateIfReset(environment);
        updateGrid(environment, true);
        moveCount++;
        if (DEBUG)
            System.out.println(moveCount);



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
            if(environment.hasFlag(AgentEnvironment.ENEMY_TEAM)){
                if(environment.isFlagNorth(AgentEnvironment.OUR_TEAM, true)){
                    move = AgentAction.MOVE_NORTH;
                }
                else if(environment.isFlagSouth(AgentEnvironment.OUR_TEAM, true)){
                    move = AgentAction.MOVE_SOUTH;
                }
                else if(environment.isFlagEast(AgentEnvironment.OUR_TEAM, true)){
                    move = AgentAction.MOVE_EAST;
                }
                else if(environment.isFlagWest(AgentEnvironment.OUR_TEAM, true)){
                    move = AgentAction.MOVE_WEST;
                }
            }
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, move);
            applyActionToLocation(loc, move);
            return move;
        }
        else if(environment.hasFlag()){
            // this agent has the flag
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
                        if(DEBUG){
                            System.out.format("Agent: %s TAGGING\n", this);
                            System.out.format("OUR COST: %d ENEMY COST: %d\n", ourCost, enemyCost);
                        }
                        prevState = new PreviousState(new Location(loc.row, loc.col), environment, move);
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
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, move);
                applyActionToLocation(loc, move);
                return move;
            }
            else{
                // seek out the enemy with the flag
                goalEast = environment.isFlagEast(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseEast(AgentEnvironment.OUR_TEAM, true);
                goalNorth = environment.isFlagNorth(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseNorth(AgentEnvironment.OUR_TEAM, true);
                goalWest = environment.isFlagWest(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseWest(AgentEnvironment.OUR_TEAM, true);
                goalSouth = environment.isFlagSouth(AgentEnvironment.OUR_TEAM, false) && !environment.isBaseSouth(AgentEnvironment.OUR_TEAM, true);


                // check immediate tiles for enemy
                if(environment.isFlagEast(AgentEnvironment.OUR_TEAM, true)){
                    prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_EAST);
                    applyActionToLocation(loc, AgentAction.MOVE_EAST);
                    return AgentAction.MOVE_EAST;
                }
                if(environment.isFlagNorth(AgentEnvironment.OUR_TEAM, true)){
                    prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
                    applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                    return AgentAction.MOVE_NORTH;
                }
                if(environment.isFlagWest(AgentEnvironment.OUR_TEAM, true)){
                    prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_WEST);
                    applyActionToLocation(loc, AgentAction.MOVE_WEST);
                    return AgentAction.MOVE_WEST;
                }

                if(environment.isFlagSouth(AgentEnvironment.OUR_TEAM, true)){
                    prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
                    applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                    return AgentAction.MOVE_SOUTH;
                }
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
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
            applyActionToLocation(loc, AgentAction.MOVE_NORTH);
            return AgentAction.MOVE_NORTH;
        }

        // if goal both north and east
        if( goalNorth && goalEast ) {
            // pick north or east for move with 50/50 chance
            if( Math.random() < 0.5 && !obstNorth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
                applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                return AgentAction.MOVE_NORTH;
            }
            if( !obstEast ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_EAST);
                applyActionToLocation(loc, AgentAction.MOVE_EAST);
                return AgentAction.MOVE_EAST;
            }
            if( !obstNorth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
                applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                return AgentAction.MOVE_NORTH;
            }
        }

        // if goal both north and west
        if( goalNorth && goalWest ) {
            // pick north or west for move with 50/50 chance
            if( Math.random() < 0.5 && !obstNorth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
                applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                return AgentAction.MOVE_NORTH;
            }
            if( !obstWest ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_WEST);
                applyActionToLocation(loc, AgentAction.MOVE_WEST);
                return AgentAction.MOVE_WEST;
            }
            if( !obstNorth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
                applyActionToLocation(loc, AgentAction.MOVE_NORTH);
                return AgentAction.MOVE_NORTH;
            }
        }

        // if the goal is south only, and we're not blocked
        if( goalSouth && ! goalEast && ! goalWest && !obstSouth ) {
            // move south
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
            applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
            return AgentAction.MOVE_SOUTH;
        }

        // do same for southeast and southwest as for north versions
        if( goalSouth && goalEast ) {
             if( Math.random() < 0.5 && !obstSouth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
                applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                return AgentAction.MOVE_SOUTH;
             }
             if( !obstEast ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_EAST);
                applyActionToLocation(loc, AgentAction.MOVE_EAST);
                return AgentAction.MOVE_EAST;
             }
             if( !obstSouth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
                applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                return AgentAction.MOVE_SOUTH;
             }
        }

        if( goalSouth && goalWest && !obstSouth ) {
            if( Math.random() < 0.5 ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
                applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                return AgentAction.MOVE_SOUTH;
            }
            if( !obstWest ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_WEST);
                applyActionToLocation(loc, AgentAction.MOVE_WEST);
                return AgentAction.MOVE_WEST;
            }
            if( !obstSouth ) {
                prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
                applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
                return AgentAction.MOVE_SOUTH;
            }
        }

        // if the goal is east only, and we're not blocked
        if( goalEast && !obstEast ) {
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_EAST);
            applyActionToLocation(loc, AgentAction.MOVE_EAST);
            return AgentAction.MOVE_EAST;
        }

        // if the goal is west only, and we're not blocked
        if( goalWest && !obstWest ) {
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_WEST);
            applyActionToLocation(loc, AgentAction.MOVE_WEST);
            return AgentAction.MOVE_WEST;
        }

        // otherwise, make any unblocked move
        if( !obstNorth ) {
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_NORTH);
            applyActionToLocation(loc, AgentAction.MOVE_NORTH);
            return AgentAction.MOVE_NORTH;
        }
        else if( !obstSouth ) {
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_SOUTH);
            applyActionToLocation(loc, AgentAction.MOVE_SOUTH);
            return AgentAction.MOVE_SOUTH;
        }
        else if( !obstEast ) {
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_EAST);
            applyActionToLocation(loc, AgentAction.MOVE_EAST);
            return AgentAction.MOVE_EAST;
        }
        else if( !obstWest ) {
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.MOVE_WEST);
            applyActionToLocation(loc, AgentAction.MOVE_WEST);
            return AgentAction.MOVE_WEST;
        }
        else {
            // completely blocked!
            prevState = new PreviousState(new Location(loc.row, loc.col), environment, AgentAction.DO_NOTHING);
            applyActionToLocation(loc, AgentAction.DO_NOTHING);
            return AgentAction.DO_NOTHING;
        }
    }


    public void applyActionToLocation(Location loc, int action){
        if(DEBUG)
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


    /*
        Get the updated tile for the given tile.
    */
    private static TileType GetTileUpdate(TileType tile){
        if(tile == TileType.TEMP_OBSTACLE){
          if(Math.random() <= TILE_RESET_PROBABILITY){
            return TileType.EMPTY;
          }
        }
        return tile;
    }


    /*
        Update the grid with data provided by the environment.
    */
    public void updateGrid(AgentEnvironment environment, boolean includeEnemies){
        // update tiles
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                grid[row][col] = GetTileUpdate(grid[row][col]);
            }
        }

        // set our previous location to empty
        if(moveCount > 0){
          grid[prevState.loc.row][prevState.loc.col] = TileType.EMPTY;
        }

        // update the grid according to the environment around us
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

        // The base tiles could have been set to empty. Always set them back to base
        if(westTeam){
            grid[5][0] = TileType.OUR_BASE;
            grid[5][9] = TileType.ENEMY_BASE;
        }
        else{
            grid[5][0] = TileType.ENEMY_BASE;
            grid[5][9] = TileType.OUR_BASE;
        }
        // update the grid with agent info
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
                grid[loc.row][loc.col+1] = TileType.TEMP_OBSTACLE;
            if(loc.col > 0 && environment.isAgentWest(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row][loc.col-1] = TileType.TEMP_OBSTACLE;
            if(loc.row > 0 && environment.isAgentNorth(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row-1][loc.col] = TileType.TEMP_OBSTACLE;
            if(loc.row < 9 && environment.isAgentSouth(AgentEnvironment.ENEMY_TEAM, true))
                grid[loc.row+1][loc.col] = TileType.TEMP_OBSTACLE;
        }
        //grid[loc.row][loc.col] = TileType.FRIENDLY;
    }


    /*
      Check if the Agent's location was reset. If so, update the location.
    */
    public void updateIfReset(AgentEnvironment environment){
        if(environment.isBaseEast(AgentEnvironment.OUR_TEAM, false) || environment.isBaseWest(AgentEnvironment.OUR_TEAM, false))
            return;

        // the agent is on it's edge of the grid
        if(environment.isObstacleNorthImmediate()){
            if(DEBUG)
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
            if(DEBUG)
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


    /*
        Get the cost to move from loc to newloc. Implemented using A*.
        It's assumed that unknown tiles are empty.
    */
    public int getMovementCost(final Location loc, final Location newLoc){
        HashSet<Location> checkedLocs = new HashSet<Location>();
        // min-heap used to drive our path search
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
            if (currentLoc.loc.equals(newLoc)) {
                // success
                return currentLoc.cost;
            }
            checkedLocs.add(currentLoc.loc);
            for (SearchLocation tempLoc : currentLoc.getSuccessors(true)) {
                if (checkedLocs.contains(tempLoc.loc)) {
                    continue;
                }
                checkedLocs.add(tempLoc.loc);
                queue.offer(tempLoc);
            }
        }
        // no path found
        return Integer.MAX_VALUE;
    }


    /*
        Get the next best move from the current location to newLoc.
        Implemented using A*. It is assumed that unknown tiles are empty.
    */
    public int getMoveToLocation(final Location newLoc, boolean hasFlag) {
        HashSet<Location> checkedLocs = new HashSet<Location>();
        // min-heap used to drive the path search
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
            if (currentLoc.loc.equals(newLoc)) {
                // success
                return currentLoc.action;
            }
            checkedLocs.add(currentLoc.loc);
            for (SearchLocation tempLoc : currentLoc.getSuccessors(hasFlag)) {
                if (checkedLocs.contains(tempLoc.loc)) {
                    continue;
                }
                checkedLocs.add(tempLoc.loc);
                queue.offer(tempLoc);
            }
        }
        // no path found
        return AgentAction.DO_NOTHING;
    }


    /*
        Check if the given tile type is traversable
    */
    public static boolean IsTileTraversable(TileType tile, boolean hasFlag){
        return tile == TileType.EMPTY || tile == TileType.ENEMY_BASE
                || (tile == TileType.OUR_BASE && hasFlag) ||
                tile == TileType.UNKNOWN;
    }

    public static boolean CanMoveNorth(Location loc, boolean hasFlag){
        if(loc.row > 0 && IsTileTraversable(grid[loc.row-1][loc.col], hasFlag))
            return true;
        return false;
    }

    public static boolean CanMoveSouth(Location loc, boolean hasFlag){
        if(loc.row < 9 && IsTileTraversable(grid[loc.row+1][loc.col], hasFlag))
            return true;
        return false;
    }

    public static boolean CanMoveEast(Location loc, boolean hasFlag){
        if(loc.col < 9 && IsTileTraversable(grid[loc.row][loc.col+1], hasFlag)){
            return true;
        }
        return false;
    }

    public static boolean CanMoveWest(Location loc, boolean hasFlag){
        if(loc.col > 0 && IsTileTraversable(grid[loc.row][loc.col-1], hasFlag))
            return true;
        return false;
    }


    // class used to store the previus state of the agent
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


    // class used in the path search implementation
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


    // class used to store grid locations
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


    // enumeration of potential tile types
    public enum TileType{
        UNKNOWN, EMPTY, OBSTACLE, ENEMY_BASE, OUR_BASE, FRIENDLY,
        TEMP_OBSTACLE
    }
}
