package de.unikassel.android.sdcframework.app.facade;

/*
 * The AIDL interface for the SDCService
 */
interface ISDCService 
{
	/**
	  * Does enable or disable the service sample broadcast feature
	  * 
	  * @param doEnable
	  *          flag if sample broadcasting shall be enabled or disabled
	  */
	void doEnableSampleBroadCasting( boolean doEnable );
}
