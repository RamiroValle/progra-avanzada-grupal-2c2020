package equipoalpha.loveletter.partida.estadosjugador;

import equipoalpha.loveletter.carta.Carta;

public class EstadoDescartando implements Estado{
    private Carta cartaElegida;

    @Override
    public void ejecutar(EstadoJugador estado) {
        cartaElegida.descartar(estado.getJugador());
    }

    public void setCartaElegida(Carta cartaElegida){
        this.cartaElegida = cartaElegida;
    }
}
