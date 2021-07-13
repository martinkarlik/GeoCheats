# GeoCheats
Mobile app that predicts the geolocation of a user-taken image, modelled as a s2geometry cell encoding the latitude and longitude. For the geolocation, I am using Google's PlaNet model [1], accessed from Tensorflow Hub, converted to a tflite model. The result is communicated to the user as a marker, using Google Maps API.


# Screenshots


# References
1 - T. Weyand, I. Kostrikov, and J. Philbin, “PlaNet - Photo Geolocation with Convolutional Neural Networks,” in Computer Vision – ECCV 2016, Cham, 2016, pp. 37–55. doi: 10.1007/978-3-319-46484-8_3.
