package com.fleencorp.feen.service.common;

public interface ObjectService {
  String getFileExtension(String filename);

  String stripExtension(String filename);

  String generateRandomNameForFile(String filename);
}
