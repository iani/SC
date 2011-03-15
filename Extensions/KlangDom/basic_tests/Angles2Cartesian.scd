/* IZ 2011 03
Converting angles to cartesian.

Needed to doublecheck correctness of inverse conversion cartesian to angles, for use by VBAPSpeakerArray


Ported from VPABSpeakerArray:anglesToCartesian

			[0.70, -1.00, 0.00],
			[0.30, 1.00, 0.00],
			[0.70, 1.00, 0.00]		

*/

(
~anglesToCartesian = { | azi, ele |
	var atorad = (2 * 3.1415927 / 360);
/*		spkr.x = cos(azi * atorad) * cos(ele * atorad);
		spkr.y = sin(azi * atorad) * cos(ele * atorad);
		spkr.z = sin(ele * atorad);
*/
		[cos(azi * atorad) * cos(ele * atorad), sin(azi * atorad) * cos(ele * atorad), sin(ele * atorad)];
}
)

~anglesToCartesian.(90, 0).round(0.01);

~anglesToCartesian.(90, 0).round(0.01);


|1 |    0 |
|  0.75 |    0 |
|  0.62 |    0 |
|   0.5 |    0 |
|  0.38 |    0 |
|  0.25 |    0 |
|     0 |    0 |
| -0.25 |    0 |
| -0.38 |    0 |
|  -0.5 |    0 |
| -0.62 |    0 |
| -0.75 |    0 |
|     1 |  0.2 |
|  0.76 | 0.16 |
|  0.64 |  0.2 |
|  0.36 |  0.2 |
|  0.23 | 0.16 |
|     0 |  0.2 |
| -0.24 | 0.16 |
| -0.36 |  0.2 |
| -0.64 |  0.2 |
| -0.76 | 0.16 |
|     1 | 0.39 |
|     0 | 0.39 |