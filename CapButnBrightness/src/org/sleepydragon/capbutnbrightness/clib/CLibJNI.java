/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.sleepydragon.capbutnbrightness.clib;

public class CLibJNI {
  public final static native void Stat_st_uid_set(long jarg1, Stat jarg1_, int jarg2);
  public final static native int Stat_st_uid_get(long jarg1, Stat jarg1_);
  public final static native void Stat_st_gid_set(long jarg1, Stat jarg1_, int jarg2);
  public final static native int Stat_st_gid_get(long jarg1, Stat jarg1_);
  public final static native long new_Stat();
  public final static native void delete_Stat(long jarg1);
  public final static native int stat(String jarg1, long jarg2, Stat jarg2_);
  public final static native int chmod(String jarg1, int jarg2);
  public final static native int EACCES_get();
  public final static native int EBADF_get();
  public final static native int EFAULT_get();
  public final static native int ELOOP_get();
  public final static native int ENAMETOOLONG_get();
  public final static native int ENOENT_get();
  public final static native int ENOMEM_get();
  public final static native int ENOTDIR_get();
  public final static native int EOVERFLOW_get();
  public final static native int EPERM_get();
  public final static native int EROFS_get();
  public final static native int S_IRUSR_get();
  public final static native int S_IWUSR_get();
  public final static native int S_IXUSR_get();
  public final static native int S_IRWXU_get();
  public final static native int S_IRGRP_get();
  public final static native int S_IWGRP_get();
  public final static native int S_IXGRP_get();
  public final static native int S_IRWXG_get();
  public final static native int S_IROTH_get();
  public final static native int S_IWOTH_get();
  public final static native int S_IXOTH_get();
  public final static native int S_IRWXO_get();
  public final static native int S_ISUID_get();
  public final static native int S_ISGID_get();
  public final static native int S_ISVTX_get();
}
