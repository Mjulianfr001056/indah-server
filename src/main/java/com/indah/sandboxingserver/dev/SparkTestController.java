package com.indah.sandboxingserver.dev;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.model.*;
import lombok.var;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class SparkTestController {

    @Autowired
    private SparkSession spark;

    @Autowired
    private DBManager dbManager;

    @GetMapping("/admin")
    public ServerResponse admin() {

        HashMap<String, Integer> map = new HashMap<>();
        map.put("disetujui", 42);
        map.put("ditolak", 78);
        map.put("pending", 34);

        return new ServerResponse(map);
    }

    static Vector<User> users = new Vector<>(Arrays.asList(
            new User("101", "Rista Indah Nirmala", "rista@bps.id", Role.ADMIN),
            new User("102", "Rizky Ramadhan", "rizky@bps.id", Role.USER),
            new User("103", "Rizal Fauzi Nurrohman", "rizal@bps.id", Role.USER),
            new User("104", "Bimo Prasetyo", "bimo@bps.id", Role.USER),
            new User("105", "Gery Pratama", "gery@bps.id", Role.USER),
            new User("106", "Indah Puspita Sari", "indah@bps.id", Role.USER),
            new User("107", "Luqman Kurniawan", "luqman@bps.id", Role.USER),
            new User("108", "Tasya Kunsita Dewi", "tasya@bps.id", Role.USER),
            new User("109", "Rafi Nur Fauzi", "rafi@bps.id", Role.USER),
            new User("110", "Yoga Hayya Alfarabi", "yoga@bps.id", Role.USER)
    ));

    static Vector<KatalogData> katalog = new Vector<>(Arrays.asList(
            new KatalogData("1", "Survei Sosial Ekonomi Nasional Maret", "SUSENAS", "2021"),
            new KatalogData("2", "Survei Sosial Ekonomi Nasional September", "SUSENAS", "2021"),
            new KatalogData("3", "Survei Sosial Ekonomi Nasional Maret", "SUSENAS", "2022"),
            new KatalogData("4", "Survei Sosial Ekonomi Nasional September", "SUSENAS", "2022"),
            new KatalogData("5", "Survei Angkatan Kerja Nasional Maret", "SUSENAS", "2021"),
            new KatalogData("6", "Survei Angkatan Kerja Nasional September", "SUSENAS", "2021"),
            new KatalogData("7", "Survei Angkatan Kerja Nasional Maret", "SUSENAS", "2022"),
            new KatalogData("8", "Survei Angkatan Kerja Nasional September", "SUSENAS", "2022"),
            new KatalogData("9", "Survei Demografi dan Kesehatan Indonesia", "SDKI", "2021"),
            new KatalogData("10", "Survei Demografi dan Kesehatan Indonesia", "SDKI", "2022"),
            new KatalogData("11", "Survei Demografi dan Kesehatan Indonesia", "SDKI", "2023"),
            new KatalogData("12", "Survei Usaha Kecil Mikro Menengah Indonesia", "UMKM", "2023")
    ));

    static Vector<Perizinan> izin = new Vector<>(Arrays.asList(
            new Perizinan("1", users.get(1), Date.valueOf("2021-07-31"), katalog.get(1), StatusPerizinan.PENDING),
            new Perizinan("2", users.get(1), Date.valueOf("2022-10-23"), katalog.get(2), StatusPerizinan.DISETUJUI),
            new Perizinan("3", users.get(2), Date.valueOf("2023-11-20"), katalog.get(3), StatusPerizinan.DITOLAK),
            new Perizinan("4", users.get(7), Date.valueOf("2021-10-05"), katalog.get(0), StatusPerizinan.DISETUJUI),
            new Perizinan("5", users.get(9), Date.valueOf("2021-11-12"), katalog.get(3), StatusPerizinan.PENDING),
            new Perizinan("6", users.get(3), Date.valueOf("2022-01-03"), katalog.get(4), StatusPerizinan.DITOLAK),
            new Perizinan("7", users.get(3), Date.valueOf("2022-02-18"), katalog.get(3), StatusPerizinan.DISETUJUI),
            new Perizinan("8", users.get(0), Date.valueOf("2022-03-25"), katalog.get(6), StatusPerizinan.PENDING),
            new Perizinan("9", users.get(1), Date.valueOf("2022-04-30"), katalog.get(3), StatusPerizinan.DITOLAK),
            new Perizinan("10", users.get(4), Date.valueOf("2022-06-11"), katalog.get(9), StatusPerizinan.PENDING),
            new Perizinan("11", users.get(2), Date.valueOf("2022-07-22"), katalog.get(10), StatusPerizinan.DISETUJUI),
            new Perizinan("12", users.get(6), Date.valueOf("2022-08-29"), katalog.get(11), StatusPerizinan.PENDING)
    ));

    @GetMapping("/user")
    public ServerResponse user() {
        return new ServerResponse(users);
    }

    @GetMapping("/izin")
    public ServerResponse izin() {
        return new ServerResponse(izin);
    }


    @GetMapping("/katalog")
    public ServerResponse katalog() {
        return new ServerResponse(katalog);
    }

    @GetMapping("/table")
    public ServerResponse table() {
        var table = dbManager.getTable("ipm");

        table.show();

        return new ServerResponse("OK");
    }
}
