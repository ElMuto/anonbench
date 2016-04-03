SELECT SETSEED(0);
SELECT dataset_name, num_features, features, target, pa_min, pa_max, pa_distance, dependency_class--, random, random_rank
FROM (
	SELECT	dataset_name, num_features, features, target, pa_min, pa_max,
		pa_distance, dependency_class, random,
		Rank() OVER ( PARTITION BY
		num_features,
		dependency_class
		ORDER BY
		random ASC
		) AS random_rank
	FROM (
		SELECT
		dataset_name, num_features, features, target, pa_min, pa_max,
		pa_distance,
		(CASE
		WHEN pa_distance < 0.0  THEN '00-negative'
		WHEN pa_distance >= 0.0 AND pa_distance < 5.0  THEN '00-05'
		WHEN pa_distance >= 5.0 AND pa_distance < 10.0 THEN '05-10'
		WHEN pa_distance >= 10.0 AND pa_distance < 15.0 THEN '10-15'
		WHEN pa_distance >= 15.0 AND pa_distance < 20.0 THEN '15-20'
		WHEN pa_distance >= 20.0 AND pa_distance < 25.0 THEN '20-25'
		WHEN pa_distance >= 25.0 AND pa_distance < 30.0 THEN '25-30'
		WHEN pa_distance >= 30.0 AND pa_distance < 35.0 THEN '30-35'
		WHEN pa_distance >= 35.0 AND pa_distance < 40.0 THEN '35-40'
		WHEN pa_distance >= 40.0 AND pa_distance < 45.0 THEN '40-45'
		WHEN pa_distance >= 45.0 AND pa_distance < 50.0 THEN '45-55'
		WHEN pa_distance >= 50.0 AND pa_distance < 55.0 THEN '50-55'
		WHEN pa_distance >= 55.0 AND pa_distance < 60.0 THEN '55-60'
		WHEN pa_distance >= 60.0 AND pa_distance < 65.0 THEN '60-65'
		WHEN pa_distance >= 65.0 AND pa_distance < 70.0 THEN '65-70'
		WHEN pa_distance >= 70.0 AND pa_distance < 75.0 THEN '70-75'
		WHEN pa_distance >= 75.0 AND pa_distance < 80.0 THEN '75-80'
		WHEN pa_distance >= 80.0 AND pa_distance < 85.0 THEN '80-85'
		WHEN pa_distance >= 85.0 AND pa_distance < 90.0 THEN '55-90'
		WHEN pa_distance >= 90.0 AND pa_distance < 95.0 THEN '90-95'
		WHEN pa_distance >= 95.0 AND pa_distance <= 100.0 THEN '95-90'
		   ELSE 'Unknown' END) as dependency_class,
		random() as random
		FROM attribute_dependencies
		WHERE pa_distance >=0
	) as base_table
) as rank_table
WHERE random_rank <= 10 AND num_features <= 4
ORDER BY num_features DESC, dependency_class, random_rank, dataset_name