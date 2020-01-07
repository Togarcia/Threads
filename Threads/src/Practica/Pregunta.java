package Practica;

public class Pregunta {
	
	private String pregunta;
	private String opcion1;
	private String opcion2;
	private String opcion3;
	private String opcion4;
	private String respuesta;
	
	public Pregunta(String pregunta, String opcion1, String opcion2, String opcion3, String opcion4, String respuesta){
		this.pregunta = pregunta;
		this.opcion1 = opcion1;
		this.opcion2 = opcion2;
		this.opcion3 = opcion3;
		this.opcion4 = opcion4;
		this.respuesta = respuesta;
	}

	public String getPregunta() {
		return pregunta;
	}

	public String getOpcion1() {
		return opcion1;
	}

	public String getOpcion2() {
		return opcion2;
	}

	public String getOpcion3() {
		return opcion3;
	}

	public String getOpcion4() {
		return opcion4;
	}

	public String getRespuesta() {
		return respuesta;
	}
	
	public String toString(){
		String cadena;
		cadena = "La pregunta es: " + pregunta
				+ "\nOpcion 1: " + opcion1
				+ "\nOpcion 2: " + opcion2
				+ "\nOpcion 3: " + opcion3
				+ "\nOpcion 4: " + opcion4;
		return cadena;
	}

}
