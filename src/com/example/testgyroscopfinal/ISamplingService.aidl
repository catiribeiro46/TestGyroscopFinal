package com.example.testgyroscopfinal;

interface ISamplingService {
  void setCallback( in IBinder binder );
  void removeCallback();
  void stopSampling();
  boolean isSampling();
  int getState();
}
