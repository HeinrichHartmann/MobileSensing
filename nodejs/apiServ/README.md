API Documentation
=================

The main API server is running at http://mobile-sensing.west.uni-koblenz.de:8888.
It is a rest-like API returning serialized data in JSON format.

Device Information
------------------

### List
It is possible to get all registered devices using a request to http://mobile-sensing.west.uni-koblenz.de:8888/devices .
The JSON return is a Array of Device objects.
A device object is of the following format:
{
  uuid: Number,           -> This number is used everywhere to reference this device
  textuuid: String,       -> 
  device: String,         ->
  fingerprint: String,    ->
  id: String,             ->
  manufacturer: String,   -> The device manufacturer
  model: String,          -> The device model
  product: String,        -> 
  androidVersion: String  -> The android os version
}

```bash
$ curl http://mobile-sensing.west.uni-koblenz.de:8888/devices

[{"uuid":1,"textuuid":"7466016c-0d8f-4aa6-94f2-9343218e8119","device":"shooteru","fingerprint":"htc_europe/htc_shooteru/shooteru:4.0.3/IML74K/385730.1:user/release-keys","id":"IML74K","manufacturer":"HTC","model":"HTC EVO 3D X515m","product":"htc_shooteru","androidVersion":"4.0.3"}, ...]
```

### One
Link: http://mobile-sensing.west.uni-koblenz.de:8888/devices/:uuid

To get a single device just make a request for /devices/:uuid .
The device object is the same as the above.
```bash
$ curl http://mobile-sensing.west.uni-koblenz.de:8888/devices/1

[{"uuid":1,"textuuid":"7466016c-0d8f-4aa6-94f2-9343218e8119","device":"shooteru","fingerprint":"htc_europe/htc_shooteru/shooteru:4.0.3/IML74K/385730.1:user/release-keys","id":"IML74K","manufacturer":"HTC","model":"HTC EVO 3D X515m","product":"htc_shooteru","androidVersion":"4.0.3"}]
```

GPS
---

GPS Object:
{
  uuid: Number,     -> The device uuid
  ts: Number,       -> Timestamp at which the recording took place
  accuracy: Number, -> The accuracy of the recording
  alt: Number,      -> Altitude
  lat: Number,      -> Latitude
  lon: Number,      -> Longitude
  speed: Number,    -> Speed
}

### List
Link: http://mobile-sensing.west.uni-koblenz.de:8888/gps/:uuid

This returns all GPS data for the given UUID.
You can specify the timestamp range and enforce a limit.
The timestamp range is given by query parameters `from` and `to`.
The limit is also a query parameter `limit`.

```bash
$ curl -X GET -G 'http://mobile-sensing.west.uni-koblenz.de:8888/gps/6' -d from=1366827805195 -d to=1366837563934 -d limit=5

[
  {"uuid":6,"ts":1366837073797,"accuracy":49,"alt":351,"lat":50.5873051,"lon":7.29799744,"speed":0},
  {"uuid":6,"ts":1366837083798,"accuracy":49,"alt":351,"lat":50.5873051,"lon":7.29799744,"speed":0},
  {"uuid":6,"ts":1366837093797,"accuracy":33,"alt":275,"lat":50.5858243,"lon":7.29763594,"speed":0},
  {"uuid":6,"ts":1366837103798,"accuracy":33,"alt":275,"lat":50.5858243,"lon":7.29763594,"speed":0},
  {"uuid":6,"ts":1366837113816,"accuracy":33,"alt":275,"lat":50.5858243,"lon":7.29763594,"speed":0}
]
```

### Count
Link: http://mobile-sensing.west.uni-koblenz.de:8888/gps/:uuid/count

This returns the amount of entries for the given uuid.

```bash
$ curl http://mobile-sensing.west.uni-koblenz.de:8888/gps/6/count

[{"COUNT(*)":250}]
```

### nearestTo
Link: http://mobile-sensing.west.uni-koblenz.de:8888/gps/:uuid/nearestTo/:ts/variance/:variance

This returns the nearest element to a given timestamp with a given variance.
The returned object is of the following format
{
  a: Number,    -> The absolute difference from the timestamp
  lat: Number,  -> Latitude
  lon: Number   -> Longitude
}
```bash
$ curl http://mobile-sensing.west.uni-koblenz.de:8888/gps/6/nearestTo/1366837073797/variance/100

[{"a":0,"lat":50.5873051,"lon":7.29799744}]
```

Accelerometer
-------------
Link: http://mobile-sensing.west.uni-koblenz.de:8888/accelerometer/:uuid
The accelerometer sensor uses the same API as the GPS Sensor except it has no access to the `nearestTo` route.
Accelerometer object:
{
  uuid: Number,     -> The device uuid
  ts: Number,       -> Timestamp at which the recording took place
  accx: Number,     -> X axis
  accy: Number,     -> Y axis
  accz: Number      -> Z Axis
}

Magnetometer
------------
Link: http://mobile-sensing.west.uni-koblenz.de:8888/magnetometer/:uuid
The accelerometer sensor uses the same API as the GPS Sensor except it has no access to the `nearestTo` route.
Accelerometer object:
{
  uuid: Number,     -> The device uuid
  ts: Number,       -> Timestamp at which the recording took place
  accx: Number,     -> X axis
  accy: Number,     -> Y axis
  accz: Number      -> Z Axis
}



