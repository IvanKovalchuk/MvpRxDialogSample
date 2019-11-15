# MvpRxDialogSample
This project demonstrates working with <b>mvprxdialog, mvprxfiledialog, cloud_disk</b> libraries

#mvprxdialog
  <br><b>mvprxdialog</b> is an Android library that provides dialogs for MVP architectural pattern.
  <br> This library holds dialogs:
   <ul>
      <li><b>MessageBox</b> dialog shows a message. It may have from 1 to 3 opional buttons</li>
      <li><b>InputBox</b> dialog allows to input a value</li>
   </ul>
   
#cloud_disk
   <br><b>cloud_disk</b>  is an Android library for accessing file storages. This library holds:
   <ul>
      <li>a wrapper that gives a unique interface for <b>the local and cloud file systems</b> (Yandex-disk and PCloud-disk)</li>
      <li>a <b>cache</b> subsystem for cloud files.</li>
   </ul>
   
#mvprxfiledialog
     <br><b>mvprxfiledialog</b> is an Android library based on <b>mvprxdialog</b> and <b>cloud_disk</b>. 
   This library provides file/dir choosing dialog including OAuth-authorization.
   
   In order to use these libraries (or one of them) in your Android project you need to add the following code into `build.gradle` files:
```   
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
   
```
dependencies {
  implementation 'com.github.kivSW.MvpRxLibraries:cloud_disk:1.0.2'
  implementation 'com.github.kivSW.MvpRxLibraries:mvprxdialog:1.0.2'
  implementation 'com.github.kivSW.MvpRxLibraries:mvprxfiledialog:1.0.2'
}
 ```

