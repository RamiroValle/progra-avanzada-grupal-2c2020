package equipoalpha.lovelettertest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import equipoalpha.loveletter.carta.Carta;
import equipoalpha.loveletter.carta.CartaTipo;
import equipoalpha.loveletter.jugador.EstadosJugador;
import equipoalpha.loveletter.partida.Sala;
import equipoalpha.loveletter.util.excepcion.JugadorNoValido;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JugadorTest {
    private JugadorImplTest jugador;
    private JugadorImplTest jugador2;
    private Sala sala;

    @Before
    public void setUp() {
        this.jugador = new JugadorImplTest("TesterDeJava");
        this.jugador2 = new JugadorImplTest("dummy");

        this.sala = jugador.crearSalaImpl("test");
    }

    @Test
    public void crearSala() {
        Assert.assertNull(jugador.crearSalaImpl("test"));
        Assert.assertNotNull(jugador2.crearSalaImpl("test"));
    }

    @Test
    public void unirseASala() {
        Assert.assertFalse(jugador.unirseASalaImpl(sala));
        Assert.assertTrue(jugador2.unirseASalaImpl(sala));
    }

    @Test
    public void iniciarPartida() {
        jugador2.iniciarPartida();
        Assert.assertNull(jugador2.salaActual); // no esta en ninguna sala

        jugador.iniciarPartida();
        Assert.assertNull(sala.partida); // solo 1 jugador

        jugador2.unirseASala(sala);
        jugador.iniciarPartida();
        Assert.assertNull(sala.partida); // faltan setear condiciones

        sala.setJugadorMano(jugador);
        sala.setCantSimbolosAfecto(5);

        jugador.iniciarPartida();
        //la sala puede iniciar ambos se encuentran confirmando que estan listos
        Assert.assertEquals(EstadosJugador.CONFIRMANDOINICIO, jugador.getEstado().getEstadoActual());
        Assert.assertEquals(EstadosJugador.CONFIRMANDOINICIO, jugador2.getEstado().getEstadoActual());
    }

    @Test
    public void confirmarInicio() {
        jugador2.unirseASala(sala);
        sala.setJugadorMano(jugador);
        sala.setCantSimbolosAfecto(5);

        jugador.iniciarPartida();

        jugador.confirmarInicio();
        Assert.assertEquals(EstadosJugador.ESPERANDO, jugador.getEstado().getEstadoActual());

        jugador2.confirmarInicio();
        Assert.assertEquals(EstadosJugador.ESPERANDO, jugador2.getEstado().getEstadoActual());
        //ambos jugadores confirmaron la partida inicio
        Assert.assertTrue(jugador.partidaJugando.partidaEnCurso);
        Assert.assertTrue(jugador2.partidaJugando.partidaEnCurso);
    }

    @Test
    public void cancelarInicio() {
        jugador2.unirseASala(sala);
        sala.setJugadorMano(jugador);
        sala.setCantSimbolosAfecto(5);

        jugador.iniciarPartida();
        //Los jugadores se encuentran confirmando el inicio
        Assert.assertEquals(EstadosJugador.CONFIRMANDOINICIO, jugador2.getEstado().getEstadoActual());

        //Un jugador no esta listo para iniciar la partida
        jugador.cancelarInicio();

        //La partida no inicia
        Assert.assertEquals(EstadosJugador.ESPERANDO, jugador2.getEstado().getEstadoActual());
        Assert.assertNull(sala.partida);
    }

    @Test
    public void onComienzoTurno() {
        jugador.carta1 = new Carta(CartaTipo.GUARDIA);
        jugador.onComienzoTurno(new Carta(CartaTipo.GUARDIA));
        Assert.assertNotNull(jugador.carta2);
        Assert.assertEquals(CartaTipo.GUARDIA, jugador.carta2.getTipo());
    }

    @Test
    public void descartarCarta1() {
        jugador.getEstado().setEstadoActual(EstadosJugador.ESPERANDO);
        Assert.assertFalse(jugador.descartarCarta1Impl());
    }

    @Test
    public void descartarCarta2() {
        jugador.getEstado().setEstadoActual(EstadosJugador.ESPERANDO);
        Assert.assertFalse(jugador.descartarCarta2Impl());
    }

    @Test
    public void robarCarta() {
        jugador.carta1 = new Carta(CartaTipo.PRINCIPE);
        jugador.robarCarta(new Carta(CartaTipo.CONDESA));
        Assert.assertEquals(EstadosJugador.DESCARTANDOCONDESA, jugador.getEstado().getEstadoActual());

        jugador.carta1 = new Carta(CartaTipo.REY);
        jugador.robarCarta(new Carta(CartaTipo.CONDESA));
        Assert.assertEquals(EstadosJugador.DESCARTANDOCONDESA, jugador.getEstado().getEstadoActual());

        jugador.carta1 = new Carta(CartaTipo.GUARDIA);
        jugador.robarCarta(new Carta(CartaTipo.CONDESA));
        Assert.assertEquals(EstadosJugador.DESCARTANDO, jugador.getEstado().getEstadoActual());
    }

    @Test
    public void elegirJugador() throws JugadorNoValido {
        sala.agregarJugador(jugador2);
        sala.setJugadorMano(jugador);
        sala.setCantSimbolosAfecto(5);
        sala.crearPartida();
        sala.partida.initPartida();
        sala.partida.onNuevaRonda(sala.partida.getJugadorMano());
        jugador.getEstado().cartaDescartada(new Carta(CartaTipo.GUARDIA));

        jugador.getEstado().setEstadoActual(EstadosJugador.ESPERANDO);
        jugador.elegirJugadorImpl(jugador2);
        Assert.assertNull(jugador.getEstado().getJugadorElegido());

        jugador.getEstado().setEstadoActual(EstadosJugador.ELIGIENDOJUGADOR);
        jugador.elegirJugadorImpl(jugador2);
        Assert.assertEquals(jugador2, jugador.getEstado().getJugadorElegido());
    }

    @Test
    public void elegirCarta() {
        jugador.getEstado().setEstadoActual(EstadosJugador.ESPERANDO);
        jugador.elegirCarta(CartaTipo.BARON);
        Assert.assertNull(jugador.getEstado().getCartaAdivinada());
    }

    @Test
    public void verCarta() {
        jugador2.carta1 = new Carta(CartaTipo.CONDESA);
        jugador.verCarta(jugador2);
        Assert.assertNotNull(jugador.getEstado().getCartaViendo());
        Assert.assertEquals(CartaTipo.CONDESA, jugador.getEstado().getCartaViendo().getTipo());
    }

    @Test
    public void tieneCarta() {
        jugador.carta1 = new Carta(CartaTipo.GUARDIA);
        Assert.assertTrue(jugador.tieneCarta(CartaTipo.GUARDIA));

        jugador.carta2 = new Carta(CartaTipo.REY);
        Assert.assertTrue(jugador.tieneCarta(CartaTipo.REY));

        Assert.assertFalse(jugador.tieneCarta(CartaTipo.CONDESA));
    }

    @Test
    public void getJugador() {
        Assert.assertEquals(jugador, jugador.getEstado().getJugador());
    }

    @Test
    public void salirSala() {
        jugador2.unirseASala(sala);
        Assert.assertTrue(sala.jugadores.contains(jugador2));
        jugador2.salirSala();
        Assert.assertFalse(sala.jugadores.contains(jugador2));
        jugador2.salirSala();
        Assert.assertNull(jugador2.salaActual);
    }

    @Test
    public void terminarAcciones() {
        jugador2.carta1 = new Carta(CartaTipo.CONDESA);
        jugador.verCarta(jugador2);
        Assert.assertNotNull(jugador.getEstado().getCartaViendo());
        Assert.assertEquals(EstadosJugador.VIENDOCARTA, jugador.getEstado().getEstadoActual());

        jugador.terminarAcciones();
        Assert.assertNull(jugador.getEstado().getCartaViendo());
        Assert.assertNotEquals(EstadosJugador.VIENDOCARTA, jugador.getEstado().getEstadoActual());
    }

    @Test
    public void serializarData() {
        jugador.carta1 = new Carta(CartaTipo.GUARDIA);
        jugador.carta2 = new Carta(CartaTipo.CONDESA);
        JsonObject json = new JsonObject();
        jugador.serializarData(json);
        Carta carta1 = new Gson().fromJson(json.get("carta1"), Carta.class);
        Assert.assertEquals(CartaTipo.GUARDIA, carta1.getTipo());
        Carta carta2 = new Gson().fromJson(json.get("carta2"), Carta.class);
        Assert.assertEquals(CartaTipo.CONDESA, carta2.getTipo());
    }

    @Test
    public void deserializarData() {
        JsonObject json = new JsonObject();
        json.add("carta1", (new Gson().toJsonTree(new Carta(CartaTipo.REY))));
        json.add("carta2", (new Gson().toJsonTree(new Carta(CartaTipo.PRINCESA))));
        json.addProperty("nombre", "jsonTest");
        json.addProperty("icono", "iconoTest");
        jugador.deserializarData(json);
        Assert.assertEquals(CartaTipo.REY, jugador.carta1.getTipo());
        Assert.assertEquals(CartaTipo.PRINCESA, jugador.carta2.getTipo());
        Assert.assertEquals("jsonTest", jugador.nombre);
        Assert.assertEquals("iconoTest", jugador.iconoNombre);
    }
}
