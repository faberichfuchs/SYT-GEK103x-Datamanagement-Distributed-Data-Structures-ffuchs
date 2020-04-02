/*
package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.Objects;

// #greeter
public class PiCalculator extends AbstractBehavior<Greeter.Greet> {

  public static final class CalculatePi {
    public final String precision;
    public final ActorRef<PiCalcualted> replyTo;

    public CalculatePi(String precision, ActorRef<PiCalcualted> replyTo) {
      this.precision = precision;
      this.replyTo = replyTo;
    }
  }

  public static final class PiCalcualted {
    public final String precision;
    public final ActorRef<CalculatePi> from;

    public PiCalcualted(String precision, ActorRef<CalculatePi> from) {
      this.precision = precision;
      this.from = from;
    }

// #greeter
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Greeted piCalcualted = (Greeted) o;
      return Objects.equals( precision, piCalcualted.whom) &&
              Objects.equals(from, piCalcualted.from);
    }

    @Override
    public int hashCode() {
      return Objects.hash( precision, from);
    }

    @Override
    public String toString() {
      return "Greeted{" +
              "whom='" + precision + '\'' +
              ", from=" + from +
              '}';
    }
// #greeter
  }

  public static Behavior<CalculatePi> create() {
    return Behaviors.setup( PiCalculator::new);
  }

  private PiCalculator(ActorContext<CalculatePi> context) {
    super(context);
  }

  @Override
  public Receive<CalculatePi> createReceive() {
    return newReceiveBuilder().onMessage(CalculatePi.class, this::onCalculate ).build();
  }

  private Behavior<Greet> onCalculate(CalculatePi command) {
    getContext().getLog().info("Hello {}!", command.precision);
    //#greeter-send-message
    command.replyTo.tell(new PiCalcualted(command.precision, getContext().getSelf()));
    //#greeter-send-message
    return this;
  }
}
// #greeter

*/
