package de.unikassel.android.sdcframework.app.facade;

/*
 * The AIDL interface for the SDCService
 */
interface ISDCService 
{	
  /**
   * Does enable or disable the sample broadcast feature
   * 
   * @param doEnable
   *          flag if sample broadcasting shall be enabled or disabled
   */
  void doEnableSampleBroadCasting( boolean doEnable );
  
  /**
   * Does activate or deactivate the sampling process for the running service.
   * This will permanent change the corresponding service setting as w
   * 
   * @param doEnable
   *          flag if sampling shall be active or not
   */
  void doEnableSampling( boolean doEnable );
  
  /**
   * Does change the persistent storage enabled state. This will permanent
   * change the corresponding service setting as well.
   * 
   * @param doEnable
   *          flag if the transfer feature shall be enabled or disabled
   */
  void doEnableSampleStorage( boolean doEnable );
  
  /**
   * Does change the sample transfer activation state. This will permanent
   * change the corresponding service setting as well.
   * 
   * @param doEnable
   *          flag if the transfer feature shall be enabled or disabled
   */
  void doEnableSampleTransfer( boolean doEnable );
  
  /**
   * Does manually trigger an instant sample transfer ( with a short delay ). If
   * the sample transfer feature is not enabled, it is activated automatically.
   * 
   * A manually triggered sample transfer does consider all configured transfer
   * settings but the frequency minimum: It will only take place if at least the
   * configured minimum of samples is available in the database, and the total
   * of transferred samples will not exceed the configured maximum.
   */
  void doTriggerSampleTransfer();
}
