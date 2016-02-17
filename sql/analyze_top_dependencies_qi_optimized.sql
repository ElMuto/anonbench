SELECT
	dataset_name,
	num_features,
	features,
	target,
	pa_min,
	pa_max,
	pa_distance
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
	    dataset_name
	ORDER BY
	    pa_distance DESC
	) AS distance_rank3
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
			fcount_rank,
			Rank() OVER ( PARTITION BY
			    dataset_name,
			    target
			ORDER BY
			    pa_distance DESC
			) AS distance_rank2
		FROM (
			SELECT fcount_rank_table.* 
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
					    dataset_name,
					    target
					ORDER BY
					    num_features ASC
					) AS fcount_rank
				FROM
					
					(SELECT
					  rs.dataset_name,
					  rs.num_features,
					  rs.features,
					  rs.target,
					  pa_min,
					  pa_max,
					  rs.pa_distance,
					  rs.distance_rank
					 FROM (
						SELECT
						  dataset_name,
						  num_features,
						  features,
						  target,
						  pa_min,
						  pa_max,
						  pa_distance,
						  Rank()
						  OVER (Partition BY
							dataset_name,
							target
						  ORDER BY
							pa_distance DESC
						  ) AS distance_rank
						FROM attribute_dependencies
						WHERE
						  pa_distance > 0
					) rs
					WHERE rs.distance_rank <= 20
					ORDER BY dataset_name, target ASC
					) distance_rank_table
				) fcount_rank_table
			WHERE fcount_rank = 1
			) fcount_rank_table2
		) distance_rank_table2
	WHERE distance_rank2 = 1
) distance_rank_table3
WHERE distance_rank3 <= 2