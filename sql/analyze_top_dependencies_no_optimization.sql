SELECT
	dataset_name,
	num_features,
	features,
	target,
	pa_min,
	pa_max,
	pa_distance,
	distance_rank2
FROM (
	SELECT
		dataset_name,
		num_features,
		features,
		target,
		pa_min,
		pa_max,
		pa_distance,
		distance_rank,
		Rank() OVER ( PARTITION BY
		    dataset_name
		ORDER BY
		    pa_distance DESC
		) AS distance_rank2
	FROM (
		SELECT
		dataset_name,
		num_features,
		features,
		target,
		pa_min,
		pa_max,
		pa_distance,	
		Rank() OVER ( PARTITION BY
		    dataset_name,
		    target
		ORDER BY
		    pa_distance DESC
		) AS distance_rank
		FROM attribute_dependencies
		WHERE dataset_name = 'Fars'
	) distance_rank_table
	WHERE distance_rank = 1
	ORDER BY pa_distance DESC
) distance_rank_table2
WHERE distance_rank2 <= 2
ORDER BY dataset_name