package pt.isec.ams.quizec.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationUtils(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Función para obtener la ubicación del usuario
    fun getUserLocation(onLocationReceived: (Location) -> Unit, onError: (Exception) -> Unit) {
        // Verificar si tenemos permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(it) // Si la ubicación es válida, pasamos la ubicación a la función onLocationReceived
                } ?: onError(Exception("Location not found"))
            }
            locationResult.addOnFailureListener { exception ->
                onError(exception) // Si ocurre un error al obtener la ubicación, pasamos el error
            }
        } else {
            // Si no se tienen los permisos, puedes devolver un error o solicitar los permisos aquí
            onError(Exception("Location permission not granted"))
        }
    }
}

// Función para calcular la distancia entre dos puntos geográficos (Formula de Haversine)
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val location1 = Location("")
    location1.latitude = lat1
    location1.longitude = lon1

    val location2 = Location("")
    location2.latitude = lat2
    location2.longitude = lon2

    return location1.distanceTo(location2) // Distancia en metros
}
