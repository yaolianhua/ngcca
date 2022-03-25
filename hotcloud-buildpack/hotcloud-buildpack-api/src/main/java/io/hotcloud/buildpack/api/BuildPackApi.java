package io.hotcloud.buildpack.api;


/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPackApi {

//    String resourceList(KanikoFlag kaniko);


    StorageResourceList storageResourceList(String namespace);


}
