package fi.tuni.prog3.wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


/*
        Wordle game
        -----------
        Player can type letters which are displayed in the screen (grid boxes),   
        and delete inserted letters that by pressing backspace.
        Player makes a guess by pressing enter (if all letters are typed in).
        Background colours of boxes then change according to the guesses:
        * grey - guessed word does not contain inserted letter at all.
        * orange - guessed word does contain inserted letter. 
        * green - inserted letter matches with the corresponding letter in the guessed word.
 */  
public class Wordle extends Application {
    private ArrayList<String> words;           // list containing words to be guessed 
    private String correctWord;                // current correct answer 
    private boolean gameActive;                // indicates if game is active  
    private final int maxMistakes = 6;         // maximum number of guesses
    private int currentGameIdx = 0;            // index keeping track of number of games played
    private Stage cur_stage;                   // current javaFX container
    private Label [][] Labels;                 // labels for grid boxes
    private int curRow;                        // location (row) of the the next inserted letter
    private int curCol;                        // location (column) of the the next inserted letter
    
    public boolean isGameActive() {
        return this.gameActive;
    }
    
    public void toggleGameActive() {
        this.gameActive = !this.gameActive;  
    }
    
    public int getCurrentGameIdx () {
        return this.currentGameIdx;
    }
    
    public void setCurrentGameIdx (int idx) {
        this.currentGameIdx= idx;
    }
    
    public void setCorrectWord(String word) {
        this.correctWord = word.toUpperCase();
    }
    
    public String getCorrectWord() {
        return this.correctWord;
    }
       
    public int getRows() {
        return (this.maxMistakes-1); 
    }
    
    public int getCols() {
        return (this.correctWord.length()-1);
    }
    public int getCurRow() {
        return this.curRow; }
    
    public int getCurCol() {
        return this.curCol;
    }    
    public Label getLabel(int row, int col) {
        return this.Labels[col][row];
    }
   
    public void setCurRow(int row) {
        this.curRow = row;
    }
    
    public void setCurCol(int col) {
        this.curCol= col;
    }
    
    public char getCellValueAsChar (int row, int col) {
        String s= this.Labels[col][row].getText();
        char c= s.toCharArray()[0];
        return c;
    }
    public String getCellValueAsString (int row, int col) {
        String s= this.Labels[col][row].getText();
        return s;
    }    
    
    // return word that the player has typed:
    public String GetCurrentWord() {
        String retStr= "";
        int row= this.curRow;
        for (int i=0;i<this.correctWord.length()-1;i++) {
            retStr= retStr + getCellValueAsString(row,i); 
        }
        return retStr;
    }    

    // initialize game by reading words to be guessed from file and storing them to list:
    public void initWordGame(String wordFilename) throws IOException {
        this.words= new ArrayList<>();
        var input = new BufferedReader(new FileReader(wordFilename));        
        String line = null;
        while( (line = input.readLine())!= null ) {
            words.add(line);
        }
        this.gameActive= false;
    }
    
    // initialize new game by resetting game state and picking up new guessed word from the list:
    public void initGame(int wordIndex) {
        int N= this.words.size(); // number of words in the list
        this.gameActive= true;    // set game active
        
        // pick next word in the list: 
        // this.setCorrectWord (this.words.get(wordIndex % N)); 
        
        // pick random word from the list:
        // (comment following two lines out to skip randomization):
        int randWordIndex= (int) (Math.random() * (N-1));
        this.setCorrectWord (this.words.get(randWordIndex));          
        this.setCurRow(0);
        this.setCurCol(0);        
    }    
    
    // initialize graphical layout: 
    public void initLayout() { 
        int i,j;
        String tempID;
        int nCols= this.correctWord.length(); // no columns in the grid
        int nRows= this.maxMistakes;          // no rows in the grid
        
        this.cur_stage.setTitle("Wordle");      
        this.cur_stage.centerOnScreen();
        this.cur_stage.getScene();
        
        // elements of the UI are grouped using FlowPane: 
        var group = new FlowPane(Orientation.VERTICAL);
        group.setPadding(new Insets(18, 18, 18, 18));
        var scene= new Scene(group, Integer.max(500, 60*nCols), 80*nRows);  

       // set button for restarting the game:        
        Button newGameBtn= new Button("Start new game");
        newGameBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        newGameBtn.setMinWidth(410);
        newGameBtn.setMaxWidth(410);
        newGameBtn.setId("newGameBtn");
        newGameBtn.setFocusTraversable(false);
        group.getChildren().add(newGameBtn);
                        
        // set label containing information on the current game:
        Label infoBox = new Label("");
        infoBox.setId("infoBox");   
        infoBox.setMinWidth(400);
        infoBox.setMaxWidth(400);    
        group.getChildren().add(infoBox);
                
        // populate grid with cells using panels:         
        GridPane grid = new GridPane();   
        grid.setHgap(10);
        grid.setVgap(10);        
        grid.setAlignment(Pos.CENTER);
        this.Labels=  new Label[nCols][nRows]; 
        // columns first:
        for (i= 0; i< nCols; i++) {
            // then rows:
            for (j=0; j<nRows; j++) {
                tempID= String.format("%s_%s", Integer.toString(j), Integer.toString(i));
                this.Labels[i][j] = new Label();
                this.Labels[i][j].setId(tempID); // set ID
                this.Labels[i][j].setText(""); // field initially blank                
                // set background:
                this.Labels[i][j].setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));   
                // set label properties:
                this.Labels[i][j].setMinWidth(50);
                this.Labels[i][j].setMinHeight(50);  
                this.Labels[i][j].setMaxWidth(50);                
                this.Labels[i][j].setMaxHeight(50);                  
                this.Labels[i][j].setStyle("-fx-border-color: black;");
                this.Labels[i][j].setFont(Font.font("Arial", FontWeight.BOLD, 24));
                this.Labels[i][j].setFocusTraversable(true);              
                this.Labels[i][j].setAlignment(Pos.CENTER);                
                // add label to grid:
                grid.add(this.Labels[i][j], i, j+1);                            
            }
        }       
            // add grid to the group:
            group.getChildren().add(grid);
            // Button event handler - start new game: 
            newGameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int idx= getCurrentGameIdx();
                idx++;
                initGame(idx);
                setCurrentGameIdx(idx);                
                initLayout();       
                }

            });

            // KEY HANDLING STARTS HERE
            scene.setOnKeyPressed(new EventHandler<KeyEvent>()             
            {
            @Override
                public void handle(KeyEvent k) {
                    // 'delete' button removes current letter (if it exists):
                    if (k.getCode() == KeyCode.BACK_SPACE && isGameActive()) {
                        int row= getCurRow(); 
                        int col= getCurCol();                                         
                        if (col >0) {
                            col--;
                            Label TF= getLabel(row, col);
                            TF.setText("");                                  
                            setCurCol(col);    
                            infoBox.setText("");                            
                        }
                    }
                    // handle 'enter' key press:
                    else if (k.getCode() == KeyCode.ENTER && isGameActive()) {
                        int row= getCurRow(); 
                        int col= getCurCol();
                        int nCols= getCols();  // no columns
                        int nRows= getRows();  // no rows
                        
                        // complete word must be inserted before resolving the guess 
                        if (col<=nCols) {
                           infoBox.setText("Give a complete word before pressing Enter!");
                        }
                        if (col == (nCols+1)) {
                            infoBox.setText("");
                            String corWord= getCorrectWord();
                            int noCorGuesses = 0;
                            for (int i=0; i<=nCols; i++) {
                                char c= getCellValueAsChar(row,i);                            
                                Label TF= getLabel(row, i);
                                // change label background color according to the guess:
                                if (c == corWord.charAt(i)) {
                                    noCorGuesses++;
                                    TF.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));                                 
                                }
                                else if (corWord.indexOf(c) >= 0) {
                                    TF.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));      
                                }
                                else {
                                    TF.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));   
                                }    
                            }    
                            // check win condition
                            if (noCorGuesses == corWord.length()) {
                                infoBox.setText("Congratulations, you won!");
                                toggleGameActive();
                            }
                            // wrong guess, check if more guesses are available
                            else if (row<nRows) {
                                row++;
                                setCurCol(0);
                                setCurRow(row);
                            }
                            // if not, it's game over
                            else {
                                infoBox.setText("Game over, you lost!");
                                toggleGameActive();
                            }
                        }                       
                    }
                    // display inserted letter in the grid: 
                    else if (k.getCode().isLetterKey() && isGameActive()) {
                        int row= getCurRow(); 
                        int col= getCurCol();
                        int colCount= getCols();                      
                        String s= k.getCode().toString();
                        if (col<=colCount) {
                            Label TF= getLabel(row, col);
                            TF.setText(s.toUpperCase());                            
                            col++;
                            setCurCol(col); 
                            infoBox.setText("");   
                        }
                    }
                }
            });               
        cur_stage.setScene(scene); 
        cur_stage.show();           
    }
    
    @Override
    public void start(Stage stage) throws IOException {
       initWordGame("words.txt");
       cur_stage= stage;              
       initGame(this.currentGameIdx); 
       initLayout(); 
    }

    public static void main(String[] args) {
        launch();
    }

}