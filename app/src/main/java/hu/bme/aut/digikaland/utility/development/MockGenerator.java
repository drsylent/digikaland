package hu.bme.aut.digikaland.utility.development;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Station;
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
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminHelpActivity;
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

    public static Bundle mockActualMain(){
        Bundle bundle = new Bundle();
        bundle.putString(ClientActualFragment.ARG_LOCATION, "Ez itt egy cím lesz");
        bundle.putString(ClientActualFragment.ARG_SUBLOCATION, "Ez itt a cím pontosítása lesz");
        bundle.putInt(ClientActualFragment.ARG_STATIONS, 10);
        bundle.putInt(ClientActualFragment.ARG_STATION_NUMBER, 7);
        Calendar c = new GregorianCalendar();
        c.set(2018,1,26,19,48);
        Date testTime = c.getTime();
        bundle.putLong(ClientActualFragment.ARG_TIME, testTime.getTime());
        return bundle;
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

    public static Bundle mockStationsList(){
        Bundle stationData = new Bundle();
        stationData.putSerializable(ClientStationsActivity.ARGS_STATIONS , stationListGenerator());
        return stationData;
    }

    public static ArrayList<Objective> mockMiniObjectiveList(){
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new PhysicalObjective("Ez csak egy picit kérdéssorozat."));
        objectives.add(new PictureObjective("Kevés kép kell csak ide.", 2));
        return objectives;
    }

    public static ArrayList<Solution> mockSolutionList(){
        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new TrueFalseSolution(new TrueFalseObjective("A BME-t 1782-ben alapították. Igaz vagy hamis?"), 0, 2, true));
        String answers[] = {"6", "7", "8", "9"};
        solutions.add(new MultipleChoiceSolution(new MultipleChoiceObjective("Hány kar található a BME-n?", answers), 0, 2, 2));
        solutions.add(new CustomAnswerSolution(new CustomAnswerObjective("Mikor alapították a VIK-et?"), 0, 2, "1923"));
        ArrayList<String> uris = new ArrayList<>();
        uris.add("file:///storage/emulated/0/Pictures/Digikaland/JPEG_20180403_155745_998781543.jpg");
        uris.add("file:///storage/emulated/0/Pictures/Digikaland/JPEG_20180329_161625_582596023.jpg");
        solutions.add(new PictureSolution(new PictureObjective("Készítsetek egy szelfit és egy képet a környezetről!", 6), 0, 3, uris));
        solutions.add(new PhysicalSolution(new PhysicalObjective("Fogj kezet!"), 0, 2));
        return solutions;
    }

    public static ArrayList<Objective> mockBigObjectiveList(){
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new TrueFalseObjective("A BME-t 1782-ben alapították. Igaz vagy hamis?"));
        String answers[] = {"6", "7", "8", "9"};
        objectives.add(new MultipleChoiceObjective("Hány kar található a BME-n?", answers));
        objectives.add(new CustomAnswerObjective("Mikor alapították a VIK-et?"));
        objectives.add(new PictureObjective("Készítsetek egy szelfit és egy képet a környezetről!", 6));
        objectives.add(new PhysicalObjective("Fogj kezet a feladat felügyelőjével!"));
        return objectives;
    }

    private static ArrayList<Station> stationListGenerator(){
        ArrayList<Station> list = new ArrayList<>();
        list.add(new Station(0, 0, Station.Status.Started, mockMiniObjectiveList()));
        list.add(new Station(2, 1, Station.Status.Done));
        list.add(new Station(4, 2, Station.Status.Done));
        list.add(new Station(1, 3, Station.Status.Started, mockBigObjectiveList()));
        list.add(new Station(3, 4, Station.Status.NotStarted));
        return list;
    }

    public static Bundle mockStatusData(){
        Bundle bundle = new Bundle();
        bundle.putString(ClientStatusFragment.ARG_RACENAME, "Ez a verseny neve");
        bundle.putString(ClientStatusFragment.ARG_TEAMNAME, "Ez a csapat neve");
        bundle.putString(ClientStatusFragment.ARG_CAPTAIN, "Ez a kapitány neve");
        bundle.putString(ClientStatusFragment.ARG_STATIONS, "Állomás: 5/7");
        bundle.putString(ClientStatusFragment.ARG_PHONE, "+11 11 111 1111");
        return bundle;
    }

    public static Bundle mockHelpData(){
        Bundle i = new Bundle();
        i.putStringArrayList(ClientHelpActivity.ARG_OBJECTADMINS, objectiveAdminNameGenerator());
        i.putStringArrayList(ClientHelpActivity.ARG_TOTALADMINS, totalAdminNameGenerator());
        i.putStringArrayList(ClientHelpActivity.ARG_OBJECTADMINPHONES, objectiveAdminPhoneGenerator());
        i.putStringArrayList(ClientHelpActivity.ARG_TOTALADMINPHONES, totalAdminPhoneGenerator());
        return i;
    }

    public static Bundle mockAdminHelpData(){
        Bundle i = new Bundle();
        i.putStringArrayList(AdminHelpActivity.ARG_OBJECTADMINS, objectiveAdminNameGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_TOTALADMINS, totalAdminNameGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_OBJECTADMINPHONES, objectiveAdminPhoneGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_TOTALADMINPHONES, totalAdminPhoneGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_OBJECTIVENAMES, objectiveNameGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_TEAMNAMES, teamNameGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_CAPTAINPHONES, captainPhoneGenerator());
        i.putStringArrayList(AdminHelpActivity.ARG_CAPTAINS, captainNameGenerator());
        return i;
    }

    private static ArrayList<String> teamNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Narancs csapat");
        list.add("Kék csapat");
        return list;
    }

    private static ArrayList<String> captainNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Farkas Flórián");
        list.add("Gézengúz Géza");
        return list;
    }

    private static ArrayList<String> captainPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+00 00 000 0000");
        list.add("+11 11 111 1111");
        return list;
    }

    private static ArrayList<String> objectiveNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Magyar tudósok körútja 1.");
        list.add("BME QB309");
        return list;
    }

    private static ArrayList<String> objectiveAdminNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Alice Aladár");
        list.add("Bob Béla");
        return list;
    }

    private static ArrayList<String> totalAdminNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Cinege Cecil");
        list.add("Dínom Dánom");
        list.add("Erik Elemér");
        return list;
    }

    private static ArrayList<String> objectiveAdminPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+01 11 111 1111");
        list.add("+12 22 222 2222");
        return list;
    }

    private static ArrayList<String> totalAdminPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+33 33 333 3333");
        list.add("+44 44 444 4444");
        list.add("+55 55 555 5555");
        return list;
    }

    public static String[] mockResultNames(){
        String[] teams = {"Narancs csapat", "Zöld csapat", "Piros csapat", "Kék csapat", "Sárga csapat", "Hupikék csapat"};
        return teams;
    }

    public static int[] mockResultPoints(){
        int[] points = {64, 23, 18, 12, 6, 2};
        return points;
    }

    private static int adminStationCycle = 1;

    public static void adminStationCycleStep(){
        adminStationCycle++;
    }

    public static boolean adminStationIsResultsActive(){ return adminStationCycle % 5 == 0; }

    public static boolean adminStationIsEnding(){
        return adminStationCycle % 3 == 0;
    }

    public static String adminStationGetNextTeamName(){
        return adminStationCycle % 2 == 0 ? "Narancs csapat" : "Kék csapat";
    }

    public static Contact adminStationGetNextContact(){
        return adminStationCycle % 2 == 0 ? new Contact("Farkas Flórián", "+00 00 000 0000") : new Contact("Gézengúz Géza", "+11 11 111 1111");
    }

    public static boolean adminStationIsToEvaluate(){
        return !(adminStationCycle % 4 == 0);
    }
}
