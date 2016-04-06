package br.com.christiano.boaviagem.boaviagem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Christiano on 15/03/2016.
 */
public class DateUtil {

    public static String deUtilParaString(java.util.Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(d);
    }

    public static java.util.Date deStringParaUtil(String d) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.parse(d);
    }

    private static String incrementarData(String d, int dias) throws ParseException {
        java.util.Date data = deStringParaUtil(d);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);
        calendar.add(Calendar.DAY_OF_MONTH, dias);

        data = calendar.getTime();

        return deUtilParaString(data);
    }

    public static int calcularDiferencaEntreDatas(String primeiraData, String segundaData) throws ParseException {
        java.util.Date data1 = deStringParaUtil(primeiraData);
        java.util.Date data2 = deStringParaUtil(segundaData);

        GregorianCalendar startTime = new GregorianCalendar();
        GregorianCalendar endTime = new GregorianCalendar();
        GregorianCalendar curTime = new GregorianCalendar();
        GregorianCalendar baseTime = new GregorianCalendar();

        startTime.setTime(data1);
        endTime.setTime(data2);

        int dif_multiplier = 1;

        // Verifica a ordem de inicio das datas
        if (data1.compareTo(data2) < 0) {
            baseTime.setTime(data2);
            curTime.setTime(data1);
            dif_multiplier = 1;
        }
        else {
            baseTime.setTime(data1);
            curTime.setTime(data2);
            dif_multiplier = -1;
        }

        int result_years = 0;
        int result_months = 0;
        int result_days = 0;

        // Para cada mes e ano, vai de mes em mes pegar o ultimo dia para import acumulando
        // no total de dias. Ja leva em consideracao ano bissesto
        while (curTime.get(GregorianCalendar.YEAR) < baseTime.get(GregorianCalendar.YEAR) ||
                curTime.get(GregorianCalendar.MONTH) < baseTime.get(GregorianCalendar.MONTH)) {
            int max_day = curTime.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
            result_months += max_day;
            curTime.add(GregorianCalendar.MONTH, 1);
        }

        // Marca que é um saldo negativo ou positivo
        result_months = result_months * dif_multiplier;

        // Retirna a diferenca de dias do total dos meses
        result_days += (endTime.get(GregorianCalendar.DAY_OF_MONTH) - startTime.get(GregorianCalendar.DAY_OF_MONTH));

        return result_years + result_months + result_days;
    }
}
