/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sargam.docfinder;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author anoop
 */
public class TextFileFilter implements FileFilter {

   @Override
   public boolean accept(File pathname) {
      return pathname.getName().toLowerCase().endsWith(".docx") || pathname.getName().toLowerCase().endsWith(".pdf");
   }
    
}
