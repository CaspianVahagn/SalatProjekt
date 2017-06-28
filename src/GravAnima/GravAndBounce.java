package GravAnima;

import static java.lang.Math.sqrt;

import java.util.ListIterator;

import Objects.Ball;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

public class GravAndBounce {

	private ObservableList<Ball> balls = FXCollections.observableArrayList();
	private final double GRAVITY = 981;
	private final double IMPULS_FACTOR = 0.78;
	private double planet[] = new double[2];
	private boolean PlanetMode;

	public GravAndBounce(ObservableList<Ball> balls) {
		this.balls = balls;
		PlanetMode = false;
	}

	public void onOffPlanet() {
		PlanetMode = !PlanetMode;
	}

	private void updatePhysics(long elapsedTime) {
		double elapsedSeconds = elapsedTime / 1_000_000_000.0;
		for (Ball b : balls) {
			if (PlanetMode) {
				b.setYVelocity(pullY(b, elapsedSeconds));
				b.setXVelocity(pullX(b, elapsedSeconds));
			} else {
				b.setYVelocity(elapsedSeconds * (GRAVITY) + b.getYVelocity());
			}

			b.setCenterX(b.getCenterX() + elapsedSeconds * b.getXVelocity());
			b.setCenterY(b.getCenterY() + elapsedSeconds * b.getYVelocity());
		}

	}

	public void speedup() {
		for (Ball b : balls) {
			if (PlanetMode) {
				b.setYVelocity(pullY(b, 0.001));
				b.setXVelocity(pullX(b, 0.001));
			} else {
				b.setYVelocity(500);
			}

		
		}
	}

	private double pullY(Ball b, double sec) {
		if (b.getCenterY() - planet[1] > 0) {
			return sec * -(GRAVITY) + b.getYVelocity();
		} else {
			return sec * (GRAVITY) + b.getYVelocity();
		}

	}

	private double pullX(Ball b, double sec) {
		if (b.getCenterX() - planet[0] > 0) {
			return sec * -(GRAVITY) + b.getXVelocity();
		} else {
			return sec * (GRAVITY) + b.getXVelocity();
		}

	}

	public void startAnimation(final Pane ballContainer) {
		final LongProperty lastUpdateTime = new SimpleLongProperty(0);
		final AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long timestamp) {
				if (lastUpdateTime.get() > 0) {
					long elapsedTime = timestamp - lastUpdateTime.get();
					checkCollisions(ballContainer.getWidth(), ballContainer.getHeight());
					updatePhysics(elapsedTime);

				}
				lastUpdateTime.set(timestamp);
			}

		};
		timer.start();
	}

	private void bounce(final Ball b1, final Ball b2, final double deltaX, final double deltaY) {
		final double distance = sqrt(deltaX * deltaX + deltaY * deltaY);
		final double unitContactX = deltaX / distance;
		final double unitContactY = deltaY / distance;

		final double xVelocity1 = b1.getXVelocity();
		final double yVelocity1 = b1.getYVelocity();
		final double xVelocity2 = b2.getXVelocity();
		final double yVelocity2 = b2.getYVelocity();

		final double u1 = xVelocity1 * unitContactX + yVelocity1 * unitContactY; 
		/**
		 * Geschwindigkeit des Ball 1 paralel zu dem Berürhngsvector von  
		 */
		
		
																					
		final double u2 = xVelocity2 * unitContactX + yVelocity2 * unitContactY; 
		/**
		 *das selbe für ball 2
		 */
																			
		final double massSum = b1.getMass() + b2.getMass();
		final double massDiff = b1.getMass() - b2.getMass();

		final double v1 = (2 * b2.getMass() * u2 + u1 * massDiff) / massSum; 
		/**
		 *  Diese Gelsicungen benötugt man für die Berechnung eindimensionaler
		 *  Kollisionen siehe gleichen für Erhaltung von Moment und konservierung
		 *  von Energie																		
		 */
		final double v2 = (2 * b1.getMass() * u1 - u2 * massDiff) / massSum; 
		
																			

		final double u1PerpX = xVelocity1 - u1 * unitContactX; 
		/**
		 * Berechnung der Geschwindigkeit in richtung 
		 * Orthogonal zum Kontakping des  Vectors
		 */
																
		final double u1PerpY = yVelocity1 - u1 * unitContactY; 
		final double u2PerpX = xVelocity2 - u2 * unitContactX; 
		/**
		 * Das selbe gilt für ball 2
		 */
		final double u2PerpY = yVelocity2 - u2 * unitContactY;

		b1.setXVelocity((v1 * unitContactX + u1PerpX) * IMPULS_FACTOR);
		b1.setYVelocity((v1 * unitContactY + u1PerpY) * IMPULS_FACTOR);
		b2.setXVelocity((v2 * unitContactX + u2PerpX) * IMPULS_FACTOR);
		b2.setYVelocity((v2 * unitContactY + u2PerpY) * IMPULS_FACTOR);

	}

	private void checkCollisions(double maxX, double maxY) {
		for (ListIterator<Ball> slowIt = balls.listIterator(); slowIt.hasNext();) {
			Ball b1 = slowIt.next();
			// check wall collisions:
			double xVel = b1.getXVelocity();
			double yVel = b1.getYVelocity();
			if ((b1.getCenterX() - b1.getRadius() <= 0 && xVel < 0)
					|| (b1.getCenterX() + b1.getRadius() >= maxX && xVel > 0)) {
				b1.setXVelocity(-xVel);
			}
			if ((b1.getCenterY() - b1.getRadius() <= 0 && yVel < 0)
					|| (b1.getCenterY() + b1.getRadius() >= maxY && yVel > 0)) {
				b1.setYVelocity(-yVel);
			}
			for (ListIterator<Ball> fastIt = balls.listIterator(slowIt.nextIndex()); fastIt.hasNext();) {
				Ball b2 = fastIt.next();
				/**
				 * Beide Methoden brauchen deltaX und Y darum in variable Packen
				 */
				final double deltaX = b2.getCenterX() - b1.getCenterX();
				final double deltaY = b2.getCenterY() - b1.getCenterY();
				if (colliding(b1, b2, deltaX, deltaY)) {
					bounce(b1, b2, deltaX, deltaY);
				}
			}
		}
	}

	public boolean colliding(final Ball b1, final Ball b2, final double deltaX, final double deltaY) {
	
		/**	Das die länge zum Quadrat zwischen den Bällen die "überlappung der Bälle
		 * und wir gucken ob diese Diestanz abnimmt hier bisschen Mathe:
		 * 
		 *  s^2 = (x2-x1)^2 + (y2-y1)^2 Distanz
		 * 	if s^2 < (r1 + r2)^2
		 * 	d/dt(s^2) < 0: Ableitung Whohoo
		 * 	2(x2-x1)(x2'-x1') + 2(y2-y1)(y2'-y1') < 0
		 */
		final double radiusSum = b1.getRadius() + b2.getRadius();
		if (deltaX * deltaX + deltaY * deltaY <= radiusSum * radiusSum) {
			if (deltaX * (b2.getXVelocity() - b1.getXVelocity())
					+ deltaY * (b2.getYVelocity() - b1.getYVelocity()) < 0) {
				return true;
			}
		}
		return false;
	}

	public double[] getPlanet() {
		return planet;
	}

	public void setPlanet(double planet[]) {
		this.planet = planet;
	}
}
