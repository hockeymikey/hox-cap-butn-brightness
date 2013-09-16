%module CLib

%{
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <errno.h>
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

// provide some the useful errno values
%constant const int EACCES;
%constant const int EBADF;
%constant const int EFAULT;
%constant const int ELOOP;
%constant const int ENAMETOOLONG;
%constant const int ENOENT;
%constant const int ENOMEM;
%constant const int ENOTDIR;
%constant const int EOVERFLOW;
