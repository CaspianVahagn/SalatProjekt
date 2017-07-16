package GUI;

import static java.lang.Math.PI;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.BROWN;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.PINK;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.YELLOW;


import java.util.Random;

import GravAnima.GravAndBounce;
import Objects.Ball;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class BallApp extends Application {
	private ObservableList<Ball> balls = FXCollections.observableArrayList();
	private GravAndBounce gb;
	private static final double MIN_RADIUS = 5;
	private static final double MAX_RADIUS = 40;
	private static final double MIN_SPEED = 50;
	private static final double MAX_SPEED = 950;
	private boolean clicki = true, planeteChoose = false;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		final Pane ballContainer = new Pane();
		Button planet = new Button("Planet");
		Button clear = new Button("clear");
		ballContainer.setStyle("-fx-background-color: rgb(24,24,24)");
		constrainBallsOnResize(ballContainer);
		gb = new GravAndBounce(balls);
		gb.setPlanet(new double[]{400,400});
        
        
        
		planet.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				
//				gb.onOffPlanet();
//				
//				
				System.out.println(c().toString());
		
				
				planet.setStyle(String.format("-fx-color: %s", c().toString().replaceAll("0x","#") ));
				
				planeteChoose = true;
				
			}
		});
		ballContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				
				if(planeteChoose){
					gb.setPlanet(new double[]{e.getX(),e.getY()});
					planeteChoose = false;
					gb.onOffPlanet();
					gb.speedup();
					planet.setStyle("-fx-color: white");
					
				}else{
					
					Random ra = new Random();
					double radius = MIN_RADIUS + (MAX_RADIUS-MIN_RADIUS) * ra.nextDouble();
		            double mass = Math.pow((radius / 40), 3);
		            
		            final double speed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * ra.nextDouble();
		            final double angle = 2 * PI * ra.nextDouble();
					
					double x = e.getX();
					double y = e.getY();
					Ball b = new Ball(x, y, radius, speed*cos(angle), speed*sin(angle), mass);
					
					b.getView().setFill(c());
					balls.add(b);
				}
				
				
				
				
			}
		});

		final BorderPane root = new BorderPane();
		final Label stats = new Label();
		root.setRight(clear);
		root.setTop(planet);
		root.setCenter(ballContainer);
		clear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				ballContainer.getChildren().clear();
				
			}
		});

		final Scene scene = new Scene(root, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
		balls.addListener(new ListChangeListener<Ball>() {
            @Override
            public void onChanged(Change<? extends Ball> change) {
                while (change.next()) {
                    for (Ball b : change.getAddedSubList()) {
                        ballContainer.getChildren().add(b.getView());
                    }
                    for (Ball b : change.getRemoved()) {
                        ballContainer.getChildren().remove(b.getView());
                    }
                    if(clicki) gb.startAnimation(ballContainer);
                    clicki = false;
                }
            }
        });
		
	}
	
    private void constrainBallsOnResize(final Pane ballContainer) {
        ballContainer.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                if (newValue.doubleValue() < oldValue.doubleValue()) {
                    for (Ball b : balls) {
                        double max = newValue.doubleValue() - b.getRadius();
                        if (b.getCenterX() > max) {
                            b.setCenterX(max);
                        }
                    }
                }
            }
        });
        
    }
	public Color c(){
		return Color.rgb(r(), r(), r());
	}
	public int r(){
		return (int) (Math.random()*255);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
