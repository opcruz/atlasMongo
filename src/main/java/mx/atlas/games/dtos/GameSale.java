package mx.atlas.games.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
@EqualsAndHashCode
public class GameSale {
    @BsonId()
    private ObjectId id;
    private int rank;
    private String title;
    private long sales;
    private String series;
    private String platforms;
    @BsonProperty("release_date")
    private String releaseDate;
    private String developers;
    private String publishers;
    @BsonIgnore
    private boolean insert = false;

    public GameSale copy() {
        GameSale gameSale = new GameSale();
        gameSale.setId(new ObjectId());
        gameSale.setRank(rank);
        gameSale.setTitle(title);
        gameSale.setSales(sales);
        gameSale.setSeries(series);
        gameSale.setPlatforms(platforms);
        gameSale.setReleaseDate(releaseDate);
        gameSale.setDevelopers(developers);
        gameSale.setPublishers(publishers);

        return gameSale;
    }

}
