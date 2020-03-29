package pt.tecnico.sauron.silo.domain;



public class Camera{

    private String _name;
    private double _lat;
    private double _long;

    public Camera() {
    }

    public Camera(String _name, double _lat, double _long) {
        this._name = _name;
        this._lat = _lat;
        this._long = _long;
    }




    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_long() {
        return _long;
    }

    public void set_long(double _long) {
        this._long = _long;
    }
}
