package hu.bme.aut.digikaland.utility.development;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveTeam;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.station.Station;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.entities.station.StationClientPerspective;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.enumeration.EvaluationStatus;
import hu.bme.aut.digikaland.entities.enumeration.StationStatusFromClient;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.entities.objectives.solutions.CustomAnswerSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.MultipleChoiceSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PhysicalSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.objectives.solutions.TrueFalseSolution;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminEvaluateActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminHelpActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationSummaryActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationsActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminTeamsActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientHelpActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientStationsActivity;
import hu.bme.aut.digikaland.ui.client.fragments.ClientActualFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;

public class MockGenerator {
    public static Dialog introDialog(Context context, DialogInterface.OnClickListener listener){
        return new AlertDialog.Builder(context).setTitle("Digikaland leírása").setMessage(
                "Üdvözöllek a Digikaland című projektem tesztelésénél! Köszönöm, hogy segíted a munkámat. " +
                        "Ennek az alkalmazásnak a segítségével egy hagyományos akadályversenyt félig-meddig digitális környezetbe ültetve is le lehet bonyolítani. " +
                        "A csapatok együtt mennek állomásokhoz, ahol feladatokat kapnak a telefonjukra, majd azon ezt megoldják, és elküldik javításra. " +
                        "A verseny során a felügyelők értékelik a beadott feladatokat, és ez alapján egy végeredmény elérhető lesz, melyet mindenki lát majd. " +
                        "Illetve lehetőség van saját verseny készítésére is! " +
                        "Jó tesztelést!"
        ).setPositiveButton("Ok", listener).create();
    }

    public static Dialog milestoneOneDialog(Context context){
        return new AlertDialog.Builder(context).setTitle("Digikaland - Kliens teszt").setMessage(
                "Ez az alkalmazás első része, mely kész van - a versenyzők grafikus felületét láthatod magad előtt nemsoká. " +
                        "A \"First Enter\" menüpont segítségével a regisztrálást végezheted el, akárcsak egy igazi verseny során majd " +
                        "a \"Client Enter\" segítségével pedig ezt kikerülve egyből a kliens grafikus felületre juthatsz el. " +
                        "A kliens mód főmenüjében a jobb felső sarokban láthatod a frissítés gombot, ezzel tudod szimulálni, hogy jelenleg mit láthatnál " +
                        "az aktuális rész alatt (mész valahova épp, feladat van folyamatban, vagy már az eredmények ki vannak hirdetve). " +
                        "Az alkalmazás jelenlegi állapotában csakis grafikus felületi elemeket tartalmaz, érdemi logikát még ne várj tőle! " +
                        "Lesznek is inkonzisztenciák a feladatok számai között például, satöbbi, ezekkel ne foglalkozz. " +
                        "A feladatod röviden annyi, hogy nyomkodd kényed-kedved szerint az alkalmazást, ahogy csak szeretnéd és kívánod. " +
                        "Az a cél, hogy kiderüljön, hol romlik el az alkalmazás jelenleg, milyen felületi változásokra reagál helytelenül, és omlik össze. " +
                        "Ha sikerült összeomlasztani, akkor mindenképp mondd el nekem, hogy mivel érted azt el! " +
                        "Ha ezzel megvagy, utána nézd végig az alkalmazás menüpontjait, elég intuitív-e a felhasználói felület, te megtalálnád-e a dolgokat, amiket szeretnél, stb. " +
                        "Ha van javaslatod ezzel kapcsolatban, hogy ezt javítsam, szintén szólj nekem. " +
                        "Üdvözlettel: Csendes Dávid"
        ).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
    }

    public static Dialog milestoneTwoDialog(Context context){
        return new AlertDialog.Builder(context).setTitle("Digikaland - Admin teszt").setMessage(
                "Ez az alkalmazás második része, mely kész van - kész van benne a versenyzők grafikus felülete, illetve a felügyelők felülete. " +
                        "Az \"Admin Total Enter\" menüpont segítségével a teljes admin felületet láthatod, ahol mindenhez hozzá tudsz férni, " +
                        "az \"Admin Station Enter\" segítségével pedig egy állomásnak az adminja lehetsz. " +
                        "Teljes adminként minden adathoz hozzáférsz a versenyen belül, minden beadott feladathoz, nyomon követheted a csapatok haladását, " +
                        "és te indíthatod el a versenyt, illetve állíthatod le, indíthatod el az eredményhirdetést. " +
                        "Állomás adminként javítani csak a saját állomásodhoz tartozó feladatokat tudod, de itt is nyomon tudod követni a verseny alakulását." +
                        "(Jelenleg ez, hogy a javítást csak a saját állomásodnál tudod elvégezni, még nincs helyesen megoldva.)" +
                        "Az alkalmazás jelenlegi állapotában csakis grafikus felületi elemeket tartalmaz, érdemi logikát még ne várj tőle! " +
                        "Lesznek is inkonzisztenciák a feladatok számai között például, satöbbi, ezekkel ne foglalkozz. " +
                        "A feladatod röviden annyi, hogy nyomkodd kényed-kedved szerint az alkalmazást, ahogy csak szeretnéd és kívánod. " +
                        "Az a cél, hogy kiderüljön, hol romlik el az alkalmazás jelenleg, milyen felületi változásokra reagál helytelenül, és omlik össze. " +
                        "Ha sikerült összeomlasztani, akkor mindenképp mondd el nekem, hogy mivel érted azt el! " +
                        "Ha ezzel megvagy, utána nézd végig az alkalmazás menüpontjait, elég intuitív-e a felhasználói felület, te megtalálnád-e a dolgokat, amiket szeretnél, stb. " +
                        "Ha van javaslatod ezzel kapcsolatban, hogy ezt javítsam, szintén szólj nekem. " +
                        "Üdvözlettel: Csendes Dávid"
        ).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
    }

    public static Bundle mockMapData(){
        Bundle locationData = new Bundle();
        double latitudes[] = {47.473372, 52.546739, 46.538830 };
        double longitudes[] = {19.059731, 13.218779, 24.558987 };
        locationData.putDoubleArray(MapsActivity.ARGS_LATITUDE, latitudes);
        locationData.putDoubleArray(MapsActivity.ARGS_LONGITUDE, longitudes);
        return locationData;
    }

    public static Bundle mockMapBigData(){
        Bundle locationData = new Bundle();
        double latitudes[] = {47.473372, 47.47338, 52.546739, 46.538830 };
        double longitudes[] = {19.059731, 19.06, 13.218779, 24.558987 };
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < 4; i++) ids.add(i);
        locationData.putIntegerArrayList(MapsActivity.MARKER_IDS, ids);
        locationData.putDoubleArray(MapsActivity.ARGS_LATITUDE, latitudes);
        locationData.putDoubleArray(MapsActivity.ARGS_LONGITUDE, longitudes);
        ArrayList<String> names = new ArrayList<>();
        names.add("BME1");
        names.add("BME2");
        names.add("Berlin");
        names.add("Targu Mures");
        locationData.putStringArrayList(MapsActivity.MARKER_NAMES, names);
        locationData.putInt(MapsActivity.MARKER_SPECIAL, 1);
        return locationData;
    }

    public static Intent adminStationSummaryGenerator(Intent placeData){
        placeData.putExtra(AdminStationSummaryActivity.ARG_STATUS, new EvaluationStatistics(1, 2, 3));
        placeData.putExtra(AdminStationSummaryActivity.ARG_LOCATION, new Location("Teszt helyszín", "Ami valahol van"));
        placeData.putExtra(AdminStationSummaryActivity.ARG_STATIONID, 8);
        placeData.putExtra(AdminStationSummaryActivity.ARG_CONTACT, new Contact("Illés Ilona", "+23 23 232 2323"));
        return placeData;
    }
}
