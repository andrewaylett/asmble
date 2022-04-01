module asmble.compiler {
    requires asmble.annotations;
    requires org.objectweb.asm.commons;
    requires transitive org.objectweb.asm.tree;
    requires org.objectweb.asm.util;
    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires java.scripting;
}