package Animations;

public class InGameAnimations {

    private int[][] grid;
    private int WIDTH;
    private int MID;
    private int state;

    public InGameAnimations(int width){
        this.WIDTH = width;
        this.MID = width/2;
        this.state = this.MID;
        this.grid = new int[width][width];

        for(int i = 0 ; i < this.WIDTH; i++){
            for(int j = 0 ; j < this.WIDTH; j++){
                grid[i][j] = 0;
            }
        }
    }

    public int getWidth(){
        return this.WIDTH;
    }


    public int[][] getGrid(){
        return this.grid;
    }

    public void updateGrid(){
        this.state = (this.state == 0) ? this.MID : this.state-1;
        updateState();
    }

    public void reset(){

        for(int i = 0; i < this.WIDTH; i++){
            for(int j = 0; j < this.WIDTH; j++){
                if(i == this.MID && j == this.MID){
                    grid[i][j] = 1;
                }
                else{
                    grid[i][j] = 0;
                }
            }
        }
    }

    private void updateState(){

        if(this.state+1 == this.MID){
            reset();
            return;
        }

        // fill red
        int index = this.state;
        for(int i = index; i < this.WIDTH-index; i++){
            grid[index][i] = 1;
            grid[this.WIDTH-index-1][i] = 1;

            grid[i][index] = 1;
            grid[i][this.WIDTH-index-1] = 1;
        }

        // fill yellow
        index = this.state+1;
        for(int i = index; i < this.WIDTH-index; i++){
            grid[index][i] = 2;
            grid[this.WIDTH-index-1][i] = 2;

            grid[i][index] = 2;
            grid[i][this.WIDTH-index-1] = 2;
        }

        if(this.state+1 == this.MID)
            return;

        // fill black
        index = this.state+2;
        for(int i = index; i < this.WIDTH-index; i++){
            grid[index][i] = 0;
            grid[this.WIDTH-index-1][i] = 0;

            grid[i][index] = 0;
            grid[i][this.WIDTH-index-1] = 0;
        }
    }
}
