/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */

package com.needhamsoftware.unojar;

/**
 * Interface to the controlling properties for a JarClassLoader.
 *
 * @author simon
 */
public interface IProperties {

  public void setVerbose(boolean verbose);

  public void setInfo(boolean info);

  public void setSilent(boolean silent);


}
