package edu.cornell.mannlib.vitro.webapp.dao;

import java.util.List;
import java.util.Set;

public class DummyFactory implements WebappDaoFactory{

  @Override
  public void close() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String checkURI(String uriStr) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String checkURIForEditableEntity(String uriStr) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasExistingURI(String uriStr) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getDefaultNamespace() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getNonuserNamespaces() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getPreferredLanguages() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getCommentsForResource(String resourceURI) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public WebappDaoFactory getUserAwareDaoFactory(String userURI) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUserURI() {
    // TODO Auto-generated method stub
    return null;
  }

}
