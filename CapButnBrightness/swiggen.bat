@REM Run swig to generate the C library
swig -java -package org.sleepydragon.capbutnbrightness.clib -outdir src\org\sleepydragon\capbutnbrightness\clib -o jni\CLib.c jni\CLib.i 
