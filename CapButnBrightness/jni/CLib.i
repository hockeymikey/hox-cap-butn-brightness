%module CLib

%{
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
%}

// automatically call loadLibrary(), at least by the proxy class for stat
%typemap(javacode) SWIGTYPE %{
static {
    System.loadLibrary("CLib");
}
%}

// use the Java style class name Stat for "struct stat"
%rename stat Stat;
struct stat {
    int st_uid;
    int st_gid;
};

// avoid renaming the "stat" function to "Stat" as well
%rename stat stat;
int stat(const char *path, struct stat *buf);
