/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework (Sensor Data Collection Framework) project.
 *
 * The SDCFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The SDCFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the SDCFramework.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.android.sdcframework.broadcast.facade;

import de.unikassel.android.sdcframework.util.facade.AsynchrounousSampleObserver;

/**
 * Interface for the sample broadcast service class. <br/>
 * <br/>
 * It's main purpose is to observe the sensor device manager for samples. It
 * does collect the observed samples in a thread safe queue while
 * running an asynchronous broadcast service, which does broadcast the collected
 * samples in the form of Intents to the system. That way the main
 * framework thread will not be occupied by sample processing tasks. <br/>
 * The dispatcher thread is a worker thread, which can be started, stopped and
 * restarted if necessary.
 * 
 * @see de.unikassel.android.sdcframework.devices.facade.SensorDeviceManager
 * @author Katy Hilgenberg
 * 
 */
public interface SampleBroadcastService
    extends AsynchrounousSampleObserver
{}
