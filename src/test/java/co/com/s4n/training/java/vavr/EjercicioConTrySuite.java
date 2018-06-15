package co.com.s4n.training.java.vavr;

import co.com.s4n.training.java.jdk.EjercicioConTry;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vavr.API.Success;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EjercicioConTrySuite {

    @Test
    public void testFizzBuzzWithTry(){

        List<Try<String>> l1 = new ArrayList<>();
        Stream<Integer> numbers = EjercicioConTry.streamNumbers();
        numbers.forEach(x -> {
            Try<String> res = EjercicioConTry.fizz(x)
                    .flatMap(y -> EjercicioConTry.buzz(x, y))
                    .flatMap(z -> EjercicioConTry.estaVacio(z, x));
            System.out.println(res.get());
            l1.add(res);
        });

        assertTrue(l1.get(0).isSuccess());
        //assertEquals(l1.get(0).get(),Success(1).get());
        //assertEquals(l1.get(2),Success("Fizz"));
        //assertEquals(l1.get(4),Success("Buzz"));
        //assertEquals(l1.get(14),Success("FizzBuzz"));
        assertEquals("Fizz", EjercicioConTry.fizz(3).getOrElse(""));
        assertEquals("Buzz", EjercicioConTry.buzz(5,"").getOrElse(""));
    }

    @Test
    public void testFizzBuzzWithRecover(){

        List<Try<String>> l1 = new ArrayList<>();
        Stream<Integer> numbers = EjercicioConTry.streamNumbers();
        numbers.forEach(x -> {
            Try<String> res = EjercicioConTry.fizzFailure(x).recover(Exception.class, "Recover")
                    .flatMap(y -> EjercicioConTry.buzz(x, y))
                    .flatMap(z -> EjercicioConTry.estaVacio(z, x));
            System.out.println(res.get());
            l1.add(res);
        });

        assertTrue(l1.get(0).isSuccess());
        //assertEquals(l1.get(0).get(),Success(1).get());
        //assertEquals(l1.get(2),Success("Fizz"));
        //assertEquals(l1.get(4),Success("Buzz"));
        //assertEquals(l1.get(14),Success("FizzBuzz"));
        assertEquals("Fizz", EjercicioConTry.fizz(3).getOrElse(""));
        assertEquals("Buzz", EjercicioConTry.buzz(5,"").getOrElse(""));
    }
}
