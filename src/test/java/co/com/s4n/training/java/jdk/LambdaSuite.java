package co.com.s4n.training.java.jdk;


import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnitPlatform.class)
public class LambdaSuite {

    @FunctionalInterface
    interface InterfaceDeEjemplo{
        int metodoDeEjemplo(int x, int y);
    }

    class ClaseDeEjemplo{
        public int metodoDeEjemplo1(int z, InterfaceDeEjemplo i){
            return z + i.metodoDeEjemplo(1,2);
        }

        public int metodoDeEjemplo2(int z, BiFunction<Integer, Integer, Integer> fn){
            return z + fn.apply(1,2);
        }
    }

    @Test
    public void smokeTest() {
        assertTrue(true);
    }

    @Test
    public void usarUnaInterfaceFuncional1(){

        InterfaceDeEjemplo i = (x,y)->x+y;

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo1(1,i);

        assertTrue(resultado==4);
    }

    @Test
    public void usarUnaInterfaceFuncional2(){

        BiFunction<Integer, Integer, Integer> f = (x, y) -> new Integer(x.intValue()+y.intValue());

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo2(1,f);

        assertTrue(resultado==4);
    }

    @Test
    public void usarUnaInterfaceFuncional3(){

        InterfaceDeEjemplo i = (x,y)->{
            int suma = 0;
            for (int j = 0; j < x*y; j++) {
                suma += j;
            }
            return suma;
        };

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo1(1,i);

        assertTrue(resultado==2);
    }

    @Test
    public void usarUnaInterfaceFuncional4(){

        BiFunction<Integer, Integer, Integer> f = (x, y) -> {String concat = x.toString() + y.toString();
            return concat.length();
        };

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo2(1,f);

        assertTrue(resultado==3);
    }

    class ClaseDeEjemplo2{

        public int metodoDeEjemplo2(int x, int y, IntBinaryOperator fn){
            return fn.applyAsInt(x,y);
        }
    }
    @Test
    public void usarUnaFuncionConTiposPrimitivos(){
        IntBinaryOperator f = (x, y) -> x + y;

        ClaseDeEjemplo2 instancia = new ClaseDeEjemplo2();

        int resultado = instancia.metodoDeEjemplo2(1,2,f);

        assertEquals(3,resultado);
    }

    @Test
    public void usarUnaFuncionConTiposPrimitivosError(){
        IntBinaryOperator f = (x, y) -> x + y;

        ClaseDeEjemplo2 instancia = new ClaseDeEjemplo2();
        //int resultado = instancia.metodoDeEjemplo2(1.1,2,f);
        int resultado = instancia.metodoDeEjemplo2(1,2,f);

        assertEquals(3,resultado);
    }

    class ClaseDeEjemplo3{

        public String operarConSupplier(Supplier<Integer> s){
            return "El int que me han entregado es: " + s.get();
        }
    }

    @Test
    public void usarUnaFuncionConSupplier(){
        Supplier s1 = () -> {
            System.out.println("Cuándo se evalúa esto? (1)");
            return 4;
        };

        Supplier s2 = () -> {
            System.out.println("Cuándo se evalúa esto? (2)");
            return 4;
        };

        ClaseDeEjemplo3 instancia = new ClaseDeEjemplo3();

        String resultado = instancia.operarConSupplier(s2);

        assertEquals("El int que me han entregado es: 4",resultado);
    }

    class ClaseDeEjemplo4{

        private int i = 0;

        public void operarConConsumer(Consumer<Integer> c){
            c.accept(i);
        }
    }

    class ClaseDeEjemplo5{

        public void operarConConsumer(Consumer<Integer> c, int i){ c.accept(4); }
    }

    @Test
    public void usarUnaFuncionConConsumer(){
        Consumer<Integer> c1 = x -> {
            System.out.println("Me han entregado este valor: "+x);
        };

        ClaseDeEjemplo4 instancia = new ClaseDeEjemplo4();

        instancia.operarConConsumer(c1);

    }

    @Test
    public void usarUnaFuncionConConsumer2(){
        Consumer<Integer> c1 = x -> {
            System.out.println("Me han entregado este valor: "+x);
        };

        ClaseDeEjemplo5 instancia = new ClaseDeEjemplo5();

        instancia.operarConConsumer(c1, 4);

    }

    @FunctionalInterface
    interface LambdaConConsumerAndSupplier{
        Consumer<Integer> operarConConsumerAndSupplier(Supplier<Integer> a,
                                          Supplier<Integer> b,
                                          Supplier<Integer> c);
    }

    @Test
    public void usarUnaFuncionConConsumerAndSupplier(){
        LambdaConConsumerAndSupplier lambda = (x,y,z) -> {
            Consumer<Integer> c = n -> {
                Integer suma = x.get() + y.get() + z.get() + n;
                System.out.println("La suma es: "+suma);
            };
            return  c;
        };

        Supplier s1 = () -> 1;

        Supplier s2 = () -> 2;

        Supplier s3 = () -> 3;

        Consumer<Integer> c = lambda.operarConConsumerAndSupplier(s1, s2, s3);

        c.accept(new Integer(9));
    }

    class ClaseDeEjemplo6{

        public String operarConSupplier(Supplier<Consumer<Integer>> s){
            return s.get().toString();
        }
    }

    class ClaseDeEjemplo7{

        public Consumer<Integer> operarConConsumer(Consumer<Integer> c, int i){ c.accept(4); return c;}
    }

    @Test
    public void usarSupplierConConsumer(){
        Consumer<Integer> c = n -> {
            System.out.println("El valor es: " +n);
        };

        Supplier<Consumer<Integer>> s1 = () -> {
            return c;
        };

        ClaseDeEjemplo6 instancia = new ClaseDeEjemplo6();
        ClaseDeEjemplo7 instancia2 = new ClaseDeEjemplo7();

        String resultado = instancia.operarConSupplier(s1);
        Consumer<Integer> consumer = instancia2.operarConConsumer(c, 4);
        consumer.accept(new Integer(4));

    }

}
