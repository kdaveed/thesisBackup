/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.dao;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.NewURIMaker;

public class NewURIMakerVitro implements NewURIMaker {

    private static final int MAX_ATTEMPTS = 20;
    WebappDaoFactory wdf;
    Set<String> madeURIs = new HashSet<String>();
    static Random random = new Random();
    
    public NewURIMakerVitro( WebappDaoFactory wdf){
        this.wdf = wdf;
    }
    
    @Override
    public String getUnusedNewURI(String prefixURI) throws InsertException {        
      return new String(UUID.randomUUID().toString().substring(0, 2));
    }
}
