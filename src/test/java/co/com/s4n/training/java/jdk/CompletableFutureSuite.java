package co.com.s4n.training.java.jdk;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnitPlatform.class)
public class CompletableFutureSuite {

    private void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){
            System.out.println("Problemas durmiendo hilo");
        }
    }

    public void imprimirMensaje(String mensaje){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        Date fecha = new Date();
        String str = dateFormat.format(fecha);
        System.out.println(str + " " + mensaje);
    }

    @Test
    public void t1() {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();


        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });
            System.out.println(Thread.currentThread().getName());

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();

        }

    }

    @Test
    public void t2(){
        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();
        }
    }

    @Test
    public void t3(){
        // Se puede construir un CompletableFuture a partir de una lambda Supplier (que no recibe parámetros pero sí tiene retorno)
        // con supplyAsync
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Hello";
        });

        try {
            String s = future.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){

            assertTrue(false);
        }
    }

    @Test
    public void t4(){

        int i = 0;
        // Se puede construir un CompletableFuture a partir de una lambda (Supplier)
        // con runAsync
        Runnable r = () -> {
            sleep(300);
            System.out.println("Soy impuro y no merezco existir");
        };

        // Note el tipo de retorno de runAsync. Siempre es un CompletableFuture<Void> asi que
        // no tenemos manera de determinar el retorno al completar el computo
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(r);

        try {
            voidCompletableFuture.get(500, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t5(){

        String testName = "t5";

        System.out.println(testName + " - El test (hilo ppal) esta corriendo en: "+Thread.currentThread().getName());

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenApply acepta lambdas de aridad 1 con retorno
        CompletableFuture<String> future = completableFuture
                .thenApply(s -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    sleep(500);

                    return s + " World";
                })
                .thenApply(s -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());

                    return s + "!";
                });

        try {
            assertEquals("Hello World!", future.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }


    @Test
    public void t6(){

        String testName = "t6";

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        // thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        // analice el segundo thenAccept ¿Tiene sentido?
        CompletableFuture<Void> future = completableFuture
                .thenAccept(s -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    sleep(500);
                })
                .thenAccept(s -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                });

    }

    @Test
    public void t7(){

        String testName = "t7";

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        CompletableFuture<Void> future = completableFuture
                .thenRun(() -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    sleep(500);
                })
                .thenRun(() -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                });

    }

    @Test
    public void t8(){

        String testName = "t8";

        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCompose(s -> {
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return s + " World"  ;
                    } );
                });

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    class Person{
        public String name;
        public int age;

        public Person(String name, int age){
            this.name = name;
            this.age = age;
        }
    }
    @Test
    public void testComposePersona(){
        String testName = "tPersona";

        CompletableFuture<Person> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "andres.25";
                })
                .thenCompose(s -> {
                    String[] parts = s.split("\\.");
                    String part1 = parts[0];
                    String part2 = parts[1];
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return new Person(part1,Integer.parseInt(part2)) ;
                    } );
                });

        try {
            Person person = completableFuture.get();
            assertEquals("andres", person.name);
            assertEquals(25, person.age);
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t9(){

        String testName = "t9";


        // El segundo parametro de thenCombina es un BiFunction la cual sí tiene que tener retorno.
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Hello";})
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> {
                            System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                            return " World";}),
                        (s1, s2) -> {
                            System.out.println(testName + " -combine corriendo en el thread: " + Thread.currentThread().getName());
                            return s1 + s2;}

                );

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t10(){

        String testName = "t10";

        // El segundo parametro de thenAcceptBoth debe ser un BiConsumer. No puede tener retorno.
        CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(
                        CompletableFuture.supplyAsync(() -> " World"),
                        (s1, s2) -> System.out.println(testName + " corriendo en thread: "+Thread.currentThread().getName()+ " : " +s1 + s2));

        try{
            Object o = future.get();
        }catch(Exception e){
            assertTrue(false);

        }
    }

    @Test
    public void testEnlaceConSupplyAsync(){
        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()-> "Hello",es);

        CompletableFuture<String> f2 = f.supplyAsync(()-> {
            imprimirMensaje("t11 Ejecutando a");
            sleep(500);
            return  "a";
        }).supplyAsync(()->{
            imprimirMensaje("t11 ejecutando b");
            return "b";
        });

        try {
            assertEquals(f2.get(),"b");
        }catch (Exception e){
            assertFalse(true);
        }
    }

    @Test
    public void testEnlaceConSupplyAsyncAndExecutioner(){
        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()-> "Hello",es);

        CompletableFuture<String> f2 = f.supplyAsync(()-> {
            imprimirMensaje("t11 Ejecutando a con es");
            sleep(500);
            return  "a";
        },es).supplyAsync(()->{
            imprimirMensaje("t11 ejecutando b con es");
            return "b";
        },es);

        try {
            assertEquals(f2.get(),"b");
        }catch (Exception e){
            assertFalse(true);
        }
    }

    @Test
    public void t11(){

        String testName = "t11";

        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es);

        f.supplyAsync(() -> "Hello")
                .thenCombineAsync(
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println(testName + " thenCombineAsync en Thread (1): " + Thread.currentThread().getName());
                        return " World";
                    }),
                    (s1, s2) -> {
                        System.out.println(testName + " thenCombineAsync en Thread (2): " + Thread.currentThread().getName());
                        return s1 + s2;
                    },
                    es
                );

    }

    @Test
    public void testApplyAsyncConSoloUnHilo(){

        String testName = "testApplyAsync";

        imprimirMensaje(testName + " - El test (hilo ppal) esta corriendo en: "+Thread.currentThread().getName());

        ExecutorService es = Executors.newFixedThreadPool(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es);

        CompletableFuture<String> future = f
                .thenApplyAsync(s -> {
                    sleep(500);
                    imprimirMensaje(testName + " - applySync corriendo en el thread: "+Thread.currentThread().getName());
                    return s + " World";
                },es)
                .thenApplyAsync(s -> {
                   imprimirMensaje(testName + " - applySync2 corriendo en el thread: "+Thread.currentThread().getName());
                    return s + "!";
                },es);

        try {
            assertEquals("Hello World!", future.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void testApplyAsyncConTresHilos(){

        String testName = "testApplyAsync";

        imprimirMensaje(testName + " - El test (hilo ppal) esta corriendo en: "+Thread.currentThread().getName());

        ExecutorService es = Executors.newFixedThreadPool(3);
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es);

        CompletableFuture<String> future = f
                .thenApplyAsync(s -> {
                    sleep(500);
                    imprimirMensaje(testName + " - applySync 3 hilos corriendo en el thread: "+Thread.currentThread().getName());
                    return s + " World";
                },es)
                .thenApplyAsync(s -> {
                    imprimirMensaje(testName + " - applySync2 3 hilos corriendo en el thread: "+Thread.currentThread().getName());
                    return s + "!";
                },es);

        try {
            assertEquals("Hello World!", future.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

}
