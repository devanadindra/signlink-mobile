package pendingstatus

import (
	"fmt"

	"gorm.io/gorm"
)

func UpdateOrderStatus(tx *gorm.DB, orderID string, status string) error {
	var userID string
	if err := tx.Raw("SELECT user_id FROM transactions WHERE id = ?", orderID).Scan(&userID).Error; err != nil {
		return fmt.Errorf("failed to get user_id from transaction: %w", err)
	}

	if err := tx.Exec("UPDATE transactions SET status = ? WHERE id = ?", status, orderID).Error; err != nil {
		return fmt.Errorf("failed to update paid status: %w", err)
	}

	return nil
}
