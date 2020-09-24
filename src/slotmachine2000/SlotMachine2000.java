
package slotmachine2000;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javafx.animation.*;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Purpose: to mimic a video slot machine. 
 * @author: Dylan Pitts
 * date: 6/1/2018
 */

public class SlotMachine2000 extends Application {
    
    //GUI Componentes
    private Stage Window;
    private MediaPlayer mediaPlayer;
    private Timeline timeLine = new Timeline();
    private MenuBar mnuBar;
    private ImageView view;
    private Menu mnuFile;
    private MenuItem mnuSave, mnuLoad, mnuReset, mnuExit;
    private Button btnSpin, btnOne, btnTen, btnHoundred;
    private Label lblWinnings, lblRules, lblOneX, lblTwoX, lblThreeX, 
            lblSlotOne, lblSlotTwo, lblSlotThree, lblWinningsArea, 
            lblCurrentBet, lblResult;
    
    //FileNames and Paths
    private String strFilePath = "Game.txt";
    private final String trg = "Triangle.png";
    private final String crl = "Target.png";
    private final String st = "Cross.png";
    private final String btn1 = "Button1.png";
    private final String btn2 = "Button2.png"; 
    private final String One = "1.png";
    private final String Ten = "10.png";
    private final String Houndred = "100.png";
    
    //Image Files
    private final Image ImageT = new Image (new File (trg).toURI().toString());
    private final Image ImageC = new Image (new File (crl).toURI().toString());
    private final Image ImageS = new Image (new File (st).toURI().toString());
    private final Image ImageB1 = new Image (new File (btn1).toURI().toString());
    private final Image ImageB2 = new Image (new File (btn2).toURI().toString());
    private final Image Image1 = new Image (new File (One).toURI().toString());
    private final Image Image10 = new Image (new File (Ten).toURI().toString());
    private final Image Image100 = new Image (new File (Houndred).toURI().toString());
    
    //global declaration of tools
    private Random generator = new Random();
    private DecimalFormat df = new DecimalFormat("0.00");
    
    //global Declarations
    private double earnings, bet, results;
    private String strSlotOne, strSlotTwo, strSlotThree;
    private int slotOne, slotTwo, slotThree;
    private final String Star = "Star", Circle = "Circle", 
            Triangle = "Triangle";
    private final double DOLLAR = 1, TEN_DOLLARS = 10, 
            HOUNDRED_DOLLARS = 100, DEFAULT_START = 100;

    public static void main(String[] args) {
        //Launch Start
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        //create stage
        Window = primaryStage;
        Window.setTitle("Slot Machine 2000");
        Window.setResizable(false);
        
        earnings = DEFAULT_START;      //set default money value for game start
        try {
            GameStart();  //reads default filename to get last unsaved earnings.
        } catch (IOException ex) {
            FileNotFound();
        }
        //creates center panel
        HBox center = new HBox(0);
        center.setPadding(new Insets(15, 12, 15, 12));
        center.setAlignment(Pos.CENTER);
        lblSlotOne = new Label();
        lblSlotOne.setGraphic(new ImageView(ImageT));
        lblSlotOne.setStyle("-fx-border-style: solid; -fx-border-color: black;");
        lblSlotTwo = new Label();
        lblSlotTwo.setGraphic(new ImageView(ImageC));
        lblSlotTwo.setStyle("-fx-border-style: solid; -fx-border-color: black;");
        lblSlotThree = new Label();
        lblSlotThree.setGraphic(new ImageView(ImageS));
        lblSlotThree.setStyle("-fx-border-style: solid; -fx-border-color: black;");
        btnSpin = new Button();
        btnSpin.setGraphic(new ImageView(ImageB1));     //set image to button
        
        //checks if button pressed
        btnSpin.setOnAction(e -> {
            if (earnings < 1)      //If player owns less money then needed to,
                NoMoney();         //bet runs NoMoney method.
            else if (bet <= 0)
                NoBet();          //If player didnt make a bet run NoBet method.
            else {
                TimeLine();        //runs animation method
                btnSpin.setGraphic(new ImageView(ImageB2));  //Set Image.
                PauseTransition pause = new PauseTransition( //pause for sound,
                        Duration.seconds(2));                //tranistion.
                String SpinFile = "Spin.wav";   //FileName.
                Media SpinSound = new Media(new File(SpinFile). //Sound File.
                        toURI().toString());
                mediaPlayer = new MediaPlayer(SpinSound);       //Media Player.
                mediaPlayer.play();                             //Play Sound.
                pause.setOnFinished(event -> {
                    btnSpin.setGraphic(new ImageView(ImageB1)); //Set Image.
                    timeLine.stop();                           //Stop Animation.
                    SlotRandomize();                           //run method.
                    Results();                                 //Run method.
                });
                pause.play();                    //plays animation after pause.
            }
        });
        //add componenets to panel
        center.getChildren().addAll(lblSlotOne, lblSlotTwo, lblSlotThree, btnSpin);
        
        //creates top panel
        HBox north = new HBox(15);
        north.setPadding(new Insets(40, 12, 15, 60));
        north.setAlignment(Pos.CENTER);
        lblWinnings = new Label("Players Winnings");
        lblWinningsArea = new Label("$");
        lblWinningsArea.setText('$' + Double.toString(earnings));
        lblCurrentBet = new Label("?");
        lblResult = new Label("?");
        //add compnenents to panel
        north.getChildren().addAll(lblWinnings, lblWinningsArea, 
                lblCurrentBet, lblResult);
        
        //create bottom panel
        HBox south = new HBox(20);
        south.setPadding(new Insets(15, 12, 15, 60));
        south.setAlignment(Pos.CENTER);
        
        //Activates when pressed set bet amount
        btnOne = new Button();
        btnOne.setStyle("-fx-background-radius: 5em;" +
                "-fx-min-width: 60px; " +
                "-fx-min-height: 60px; " +
                "-fx-max-width: 60px; " +
                "-fx-max-height: 60px;");
        btnOne.setGraphic(new ImageView(Image1));
        btnOne.setOnAction(e -> {
            lblCurrentBet.setText("Bet: $1.00");
            bet = DOLLAR;                           //set bet to 1.00
        });
        btnTen = new Button();
        btnTen.setStyle("-fx-background-radius: 5em;" +
                "-fx-min-width: 60px; " +
                "-fx-min-height: 60px; " +
                "-fx-max-width: 60px; " +
                "-fx-max-height: 60px;");
        btnTen.setGraphic(new ImageView(Image10));
        btnTen.setOnAction(e -> {
            lblCurrentBet.setText("Bet: $10.00");
            bet = TEN_DOLLARS;                      //set bet to 10.00
        });
        btnHoundred = new Button();
        btnHoundred.setStyle("-fx-background-radius: 5em;" +
                "-fx-min-width: 60px; " +
                "-fx-min-height: 60px; " +
                "-fx-max-width: 60px; " +
                "-fx-max-height: 60px;");
        btnHoundred.setGraphic(new ImageView(Image100));
        btnHoundred.setOnAction(e -> {
            lblCurrentBet.setText("Bet: $100.00");
            bet = HOUNDRED_DOLLARS;                 //set bet to 100.00
        });
        //set components to panel
        south.getChildren().addAll(btnOne, btnTen, btnHoundred);
        
        //creates left panel
        VBox west = new VBox(5);
        west.setPadding(new Insets(15, 12, 15, 12));
        west.setAlignment(Pos.CENTER);
        
        lblRules = new Label("Rules");
        lblOneX = new Label("3 Triangles = 1x"); 
        lblTwoX = new Label("3 Targets = 2x");
        lblThreeX = new Label("3 Crosses = 3x");
        //set components to panel
        west.getChildren().addAll(lblRules, lblOneX, lblTwoX, lblThreeX);
        
        //creates window
        BorderPane stage = new BorderPane();
        BorderPane Border = new BorderPane();
        
        Border.setTop(north);
        Border.setCenter(center);
        Border.setBottom(south);
        Border.setLeft(west);
        
        //set panel to panel
        stage.setCenter(Border);
        
        //creates menu
        mnuFile = new Menu("_File");
        
        //when pressed try WriteToFile method
        mnuSave = new MenuItem("_Save");
        mnuSave.setOnAction(e -> {
            try {
                WriteToFile();
            } catch (IOException ex) {
                FileNotFound();
            }
        });
        mnuLoad = new MenuItem("_Load");
        
        //when pressed try ReadFromFile() method
        mnuLoad.setOnAction(e -> {
            try {
                ReadFromFile();
            } catch (IOException ex) {
                FileNotFound();
            }
        });
        mnuReset = new MenuItem("_Reset");
        
        //when pressed run ResetGame() method
        mnuReset.setOnAction(e -> {
            ResetGame();
        });
        //add separator
        SeparatorMenuItem Sep = new SeparatorMenuItem();
        
        mnuExit = new MenuItem("E_xit");
        
        //when pressed close
        mnuExit.setOnAction(e -> {
            Window.close();
        });
        //add components to menu
        mnuFile.getItems().addAll(mnuSave, mnuLoad, mnuReset, 
                Sep, mnuExit);
                 
        Menu mnuAbout = new Menu("_About");
        
        //when pressed run getNotePad() method
        mnuAbout.setOnAction(e -> {
            try {
                getNotePad();
            } catch (IOException ex) {
                FileNotFound();
            }
        });
        //add component to menu
        mnuAbout.getItems().add(new MenuItem("_Rules"));
        
        mnuBar = new MenuBar();
        
        //add menus to menubar
        mnuBar.getMenus().addAll(mnuFile, mnuAbout);
        
        //add menubar to panel
        stage.setTop(mnuBar);

        Scene scene = new Scene(stage, 400, 300);
        Window.setScene(scene);
        Window.show();
        
        //when closing ask if they want to quit if yes run closeProgram()
        Window.setOnCloseRequest(e -> {
            e.consume();
            try {
                closeProgram();
            } catch (IOException ex) {
                FileNotFound();
            }
        });
    }
    
    /**
     * Randomize what is picked for each slot
     */
    public void SlotRandomize() {
            
        slotOne = generator.nextInt(6) + 1; //random number 1 between 6
        
        //if number between 1-3 set slot one to triangle
        if (slotOne == 1 || slotOne ==  2 || slotOne ==  3) {
            strSlotOne = Triangle;
            lblSlotOne.setGraphic(new ImageView(ImageT));
        }
        //if number 4 or 5 set slot one to circle
        if (slotOne == 4 || slotOne == 5) {
            strSlotOne = Circle;
            lblSlotOne.setGraphic(new ImageView(ImageC));
        }
        //if number 6 set slot one to Star
        if (slotOne == 6) {
            strSlotOne = Star;
            lblSlotOne.setGraphic(new ImageView(ImageS));
        }

        slotTwo = generator.nextInt(6) + 1; //random number 1 between 6
        
        //if number between 1-3 set slot two to triangle
        if (slotTwo == 1 || slotTwo ==  2 || slotTwo ==  3) {
            strSlotTwo = Triangle;
            lblSlotTwo.setGraphic(new ImageView(ImageT));
        }
        //if number 4 or 5 set slot two to circle
        if (slotTwo == 4 || slotTwo == 5) {
            strSlotTwo = Circle;
            lblSlotTwo.setGraphic(new ImageView(ImageC));
        }
        //if number 6 set slot two to Star
        if (slotTwo == 6) {
            strSlotTwo = Star;
            lblSlotTwo.setGraphic(new ImageView(ImageS));
        }
        
        slotThree = generator.nextInt(6) + 1; //random number 1 between 6
        
        //if number between 1-3 set slot three to triangle
        if (slotThree == 1 || slotThree ==  2 || slotThree ==  3) {
            strSlotThree = Triangle;
            lblSlotThree.setGraphic(new ImageView(ImageT));
        }
        //if number 4 or 5 set slot three to circle
        if (slotThree == 4 || slotThree == 5) {
            strSlotThree = Circle;
            lblSlotThree.setGraphic(new ImageView(ImageC));
        }
        //if number 6 set slot three to Star
        if (slotThree == 6) {
            strSlotThree = Star;
            lblSlotThree.setGraphic(new ImageView(ImageS));
        }
    }
    
    /**
     * Get Results of Randomize
     */
    public void Results() {
        //FileName & Paths
        String minorWinFile = "Level-Up.wav";
        String failureFile = "Failure.wav";
        String Jackpot = "Jackpot.wav";
        
        //Sound Files
        Media slotWinSound = new 
        Media(new File(minorWinFile).toURI().toString());
        
        Media slotFailSound = new 
        Media(new File(failureFile).toURI().toString());
        
        Media slotJackpotSound = new 
        Media(new File(Jackpot).toURI().toString());
        
        //if slot one equals slot two and slot three       
        if (strSlotOne.equals(strSlotTwo) && strSlotTwo.equals(strSlotThree)) {
            mediaPlayer = new MediaPlayer(slotJackpotSound);
            
            //if match was a triangle
            if (strSlotOne.equals(Triangle)) {
                results += (bet * 1);
                mediaPlayer.play();
            }
            //if match was a circle
            else if (strSlotOne.equals(Circle)) {
                results += (bet * 2);
                mediaPlayer.play();
            } else {
                //if match was star
                results += (bet * 3);
                mediaPlayer.play();
            }
        }//if slot one equals slot two
        else if (strSlotOne.equals(strSlotTwo) || 
                strSlotOne.equals(strSlotThree)) {
                mediaPlayer = new MediaPlayer(slotWinSound);
                
            //if match was triangle    
            if (strSlotOne.equals(Triangle)) {
                results += (bet * .25 * 1) - bet; 
                mediaPlayer.play();
            }
            //if match was circle
            else if (strSlotOne.equals("Circle")) {
                results += (bet * .25 * 2) - bet;
                mediaPlayer.play();
            } else {
                //if match was star
                results += (bet * .75 * 3) - bet;
                mediaPlayer.play();
            }
        }//if slot two matches three
        else if (strSlotTwo.equals(strSlotThree)) {
            mediaPlayer = new MediaPlayer(slotWinSound);

            //if match was triangle
            if (strSlotTwo.equals(Triangle)) {
                results += (bet * .25 * 1) - bet;
                mediaPlayer.play();
            }
            //if match was circle
            else if (strSlotTwo.equals("Circle")) {
                results += (bet * .25 * 2) - bet;
                mediaPlayer.play();
            } else {
                //if match was star
                results += (bet * .5 * 3) - bet;
                    mediaPlayer.play();
            }
        }//if no matches
        else {
            mediaPlayer = new MediaPlayer(slotFailSound);
            results -= bet;
            mediaPlayer.play();
        }
        //calculate results
        earnings += results;
        String newResult = df.format(results); //format results
        results = 0.00;                    
        
        CheckButtonState(); //checks what bet button to disable/enable
        
        lblResult.setText("Result: " + '$' + newResult);
        SetScore();         //set players money
        bet = 0;    
        lblCurrentBet.setText("Bet: $0.00");
    }
    
    /**
     * Sets Players Money
     */
    public void SetScore() {
        
        String newEarnings = df.format(earnings);
        lblWinningsArea.setText('$' + newEarnings);
    }
    
    /**
     * Animation that flicks through images 
     */
    public void TimeLine() {
        
        Collection<KeyFrame> frames = timeLine.getKeyFrames();
        Duration frameGap = Duration.millis(100);
        Duration frameTime = Duration.ZERO ;
        for (int i = 0; i < 10; i++) {
            frameTime = frameTime.add(frameGap);
            frames.add(new KeyFrame(frameTime, e -> Spin()));
        }
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }
    
    /**
     * Used in animation randomizes Images for each slot
     */
    public void Spin() {
        
      slotOne = generator.nextInt(3) + 1;    //random number between 1-3

        if (slotOne == 1)
            lblSlotOne.setGraphic(new ImageView(ImageT));
            
        if (slotOne == 2) 
            lblSlotOne.setGraphic(new ImageView(ImageC));
        
        if (slotOne == 3) 
            lblSlotOne.setGraphic(new ImageView(ImageS));
        
        slotTwo = generator.nextInt(3) + 1;

        if (slotTwo == 1) 
            lblSlotTwo.setGraphic(new ImageView(ImageT));
        
        if (slotTwo == 2) 
            lblSlotTwo.setGraphic(new ImageView(ImageC));
        
        if (slotTwo == 3) 
            lblSlotTwo.setGraphic(new ImageView(ImageS));
        
        slotThree = generator.nextInt(3) + 1;

        if (slotThree == 1) 
            lblSlotThree.setGraphic(new ImageView(ImageT));
        
        if (slotThree == 2) 
            lblSlotThree.setGraphic(new ImageView(ImageC));
        
        if (slotThree == 3) 
            lblSlotThree.setGraphic(new ImageView(ImageS));
     }
    
    /**
     * resets players money
     */
    public void ResetGame() {
        
        earnings = DEFAULT_START;
        SetScore();
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Game Reset!");
        alert.show();
    }
    
    /**
     * runs NameFile() method to name the file then write file with that name
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void WriteToFile() throws IOException {
        
        NameFile();
        
        FileWriter f1 = new FileWriter (strFilePath, false);
        BufferedWriter b1 = new BufferedWriter (f1);
        b1.write(Double.toString(earnings));
        b1.close();
    
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("File Saved!");
        alert.show();
    }
    
    /**
     * runs NameFile() method to get name of file then read file with that name
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void ReadFromFile() throws IOException {
        
        NameFile();
        
        BufferedReader reader = new BufferedReader(
                new FileReader(new File(strFilePath)));
        String line;
        while ((line = reader.readLine()) != null)
            earnings = Double.parseDouble(line);
        
        SetScore();
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("File Loaded!");
        alert.show();
        
    }
    
    /**
     * Used in try-catch if FileNotFound exception caught
     */
    public void FileNotFound() {
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error!");
        alert.setContentText("File Not Found!");

        alert.showAndWait();
    }
    
    /**
     * Ask User for file name
     */
    public void NameFile() {
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the Name of the File!");
        Optional<String> result = dialog.showAndWait();
        strFilePath = result.get();
    }
    
    /**
     * opens About.txt
     * @throws IOException 
     */
    public void getNotePad() throws IOException{
        
        String pathpdf = "About.txt";  
        String[] params = {"cmd", "/c", pathpdf};  
        Runtime.getRuntime().exec(params);  
    }
    
    /**
     * if run reset game
     */
    public void NoMoney() {
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Insufficient funds!");
        alert.setHeaderText("Resetting Score!");
        ResetGame();
        CheckButtonState();
        alert.show();
    }
    
    /**
     * if run displays message
     */
    public void NoBet() {
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("You Didnt Make A bet!");
        alert.setHeaderText("Please Make A Bet!");
        alert.show();
    }
    
    /**
     * Checks players earnings disabling/enabling button depending on earnings
     */
    public void CheckButtonState() {
        
        if (earnings < 100)
            btnHoundred.setDisable(true);
        if (earnings < 10)
            btnTen.setDisable(true);
        if (earnings < 1)
            btnOne.setDisable(true);
            
        if (earnings >= 100)
            btnHoundred.setDisable(false);
        if (earnings >= 10)
            btnTen.setDisable(false);
        if (earnings >= 1)
            btnOne.setDisable(false);
    }
    
    public void GameStart() throws IOException {
        
        BufferedReader reader = new BufferedReader(
            new FileReader(new File(strFilePath)));
        String line;
        while ((line = reader.readLine()) != null)
            earnings = Double.parseDouble(line);
    }
    
    /**
     * when runs ask if user wants to quit if yes write to default file
     * @throws IOException 
     */
    public void closeProgram() throws IOException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to exit?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            FileWriter f1 = new FileWriter (strFilePath, false);
            BufferedWriter b1 = new BufferedWriter (f1);
            b1.write(Double.toString(earnings));
            b1.close();
            Window.close();
        } else {
            
        }
    }
}

