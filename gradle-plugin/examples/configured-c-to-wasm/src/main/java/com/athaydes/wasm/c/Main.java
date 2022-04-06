package com.athaydes.wasm.c;

public class Main {
    public static void main( String[] args ) {
        if ( args.length != 2 ) {
            args = new String[2];
            args[0] = "123";
            args[1] = "234";
        }
        int a = Integer.parseInt( args[ 0 ] );
        int b = Integer.parseInt( args[ 1 ] );

        System.out.println( new Adder( 131072 ).add( a, b ) );
    }
}
